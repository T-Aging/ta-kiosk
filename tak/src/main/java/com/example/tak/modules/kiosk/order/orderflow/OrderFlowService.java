package com.example.tak.modules.kiosk.order.orderflow;

import com.example.tak.common.Menu;
import com.example.tak.common.MenuOptionRule;
import com.example.tak.common.OptionGroup;
import com.example.tak.common.OptionValue;
import com.example.tak.modules.agent.snapshot.repository.MenuOptionRuleRepository;
import com.example.tak.modules.agent.snapshot.repository.MenuRepository;
import com.example.tak.modules.kiosk.order.dto.OrderItemSaveDto;
import com.example.tak.modules.kiosk.order.dto.OrderSaveDto;
import com.example.tak.modules.kiosk.order.dto.OptionDto;
import com.example.tak.modules.kiosk.order.dto.OptionGroupDto;
import com.example.tak.modules.kiosk.order.dto.response.*;
import com.example.tak.modules.kiosk.order.repository.OptionGroupRepository;
import com.example.tak.modules.kiosk.order.repository.OptionValueRepository;
import com.example.tak.modules.kiosk.order.service.OrderSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFlowService {

    private final OrderFlowStateManager stateManager;
    private final MenuRepository menuRepository;
    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;
    private final OrderSaveService orderSaveService;
    private final MenuOptionRuleRepository menuOptionRuleRepository;

    public AskTemperatureResponse startOrder(String wsSessionId, String storeId, String menuName){
        // 세션 별 상태 가져오기
        OrderFlowState state=stateManager.getOrCreate(wsSessionId);
        state.setStoreId(storeId);

        Menu menu = menuRepository
                .findByName(menuName) // 임시
                .orElseThrow(()-> new IllegalArgumentException("메뉴를 찾을 수 없습니다: " + menuName));

        state.setMenuId(menu.getId());
        state.setMenuName(menu.getName());
        state.setStep(OrderStep.SELECT_TEMPERATURE);

        AskTemperatureResponse res= new AskTemperatureResponse();
        res.setType("ask_temperature");
        res.setMenuName(menu.getName());

        // 1) 이 메뉴에 temperature 그룹이 있는지 조회
        var tempGroupOpt = optionGroupRepository.findGroupForMenuAndKey(menu.getId(), "temperature");
        if(tempGroupOpt.isEmpty()){
            // 온도 옵션이 아예 없는 메뉴 -> 온도 질문 없이 바로 사이즈로
            state.setStep(OrderStep.SELECT_SIZE);
            res.setQuestion("온도 선택 없이 진행할게요.");
            res.setChoices(List.of());
            return res;
        }

        OptionGroup tempGroup = tempGroupOpt.get();

        // 2) temperature 그룹의 실제 옵션 값들(HOT/ICE 등)
        var tempValues=optionValueRepository
                .findByOptionGroupIdAndActiveTrueOrderBySortOrder(tempGroup.getId());

        // 3) 이 메뉴 + temperature 그룹에 대한 룰 조회 (default / forbid / recommend 등)
        var tempRules = menuOptionRuleRepository.findByMenuAndGroup(menu.getId(), tempGroup.getId());

        boolean hotForbidden = tempRules.stream()
                .anyMatch(r ->
                        r.getRuleType() == MenuOptionRule.RuleType.FORBID &&
                                "HOT".equalsIgnoreCase(r.getOptionValue().getValueKey())
                );

        boolean iceForbidden = tempRules.stream()
                .anyMatch(r ->
                        r.getRuleType() == MenuOptionRule.RuleType.FORBID &&
                                "ICE".equalsIgnoreCase(r.getOptionValue().getValueKey())
                );

        // 4) 실제로 선택 가능한지 = 값이 존재 + forbid 룰에 걸리지 않음
        boolean hotAvailable = tempValues.stream()
                .anyMatch(v -> "HOT".equalsIgnoreCase(v.getValueKey()))
                && !hotForbidden;

        boolean iceAvailable = tempValues.stream()
                .anyMatch(v -> "ICE".equalsIgnoreCase(v.getValueKey()))
                && !iceForbidden;

        // 5) 프론트에 내려줄 질문/선택지 구성
        if (hotAvailable && iceAvailable) {
            res.setQuestion("뜨겁게 드실까요, 차갑게 드실까요?");
            res.setChoices(List.of("HOT", "ICE"));
        } else if (iceAvailable) {
            res.setQuestion("이 메뉴는 ICE만 제공돼요. 아이스로 진행할까요?");
            res.setChoices(List.of("ICE"));
        } else if (hotAvailable) {
            res.setQuestion("이 메뉴는 HOT만 제공돼요. 뜨겁게 진행할까요?");
            res.setChoices(List.of("HOT"));
        } else {
            // 둘 다 불가한 데이터는 사실상 잘못된 메뉴이므로, 일단 온도 선택 없이 진행
            res.setQuestion("온도 선택 없이 진행할게요.");
            res.setChoices(List.of());
            state.setStep(OrderStep.SELECT_SIZE);
        }

        return res;
    }

    public AskSizeResponse selectTemperature(String wsSessionId, String temperature) {

        OrderFlowState state = stateManager.get(wsSessionId);
        if (state == null) {
            throw new IllegalStateException("주문 상태가 존재하지 않습니다. 먼저 order_start를 호출해야 합니다.");
        }

        state.setTemperature(temperature);
        state.setStep(OrderStep.SELECT_SIZE);

        var sizeGroupOpt = optionGroupRepository.findGroupForMenuAndKey(state.getMenuId(), "size");

        List<String> sizes;

        if (sizeGroupOpt.isPresent()) {
            var sizeGroup = sizeGroupOpt.get();
            var sizeValues = optionValueRepository.findByOptionGroupIdAndActiveTrueOrderBySortOrder(sizeGroup.getId());

            // 프론트와는 value_key 기준으로 주고받기 (R / L 등)
            sizes = sizeValues.stream()
                    .map(OptionValue::getValueKey)
                    .toList();
        } else {
            // size 그룹이 없으면 기본 사이즈 하나만 있다고 가정 (ex. "R")
            sizes = List.of("R");
        }

        AskSizeResponse res = new AskSizeResponse();
        res.setType("ask_size");
        res.setMenuName(state.getMenuName());
        res.setTemperature(temperature);
        res.setQuestion("사이즈를 선택해 주세요.");
        res.setChoices(sizes);

        return res;
    }

    public AskDetailOptionYnResponse selectSize(String wsSessionId, String size) {

        OrderFlowState state = stateManager.get(wsSessionId);
        if (state == null) {
            throw new IllegalStateException("주문 상태가 존재하지 않습니다. 먼저 order_start를 호출해야 합니다.");
        }

        state.setSize(size);
        state.setStep(OrderStep.ASK_DETAIL_OPTION_YN);

        AskDetailOptionYnResponse res = new AskDetailOptionYnResponse();
        res.setType("ask_detail_option_yn");
        res.setMenuName(state.getMenuName());
        res.setTemperature(state.getTemperature());
        res.setSize(size);
        res.setQuestion("샷 추가, 휘핑 추가 등 세부 옵션을 설정하시겠습니까?");
        res.setChoices(List.of("예", "아니오"));

        return res;
    }

    public Object answerDetailOptionYn(String wsSessionId, String answer) {

        OrderFlowState state = stateManager.get(wsSessionId);
        if (state == null) {
            throw new IllegalStateException("주문 상태가 존재하지 않습니다. 먼저 order_start를 호출해야 합니다.");
        }

        boolean yes = "예".equals(answer) || "YES".equalsIgnoreCase(answer);

        if (yes) {
            state.setStep(OrderStep.SELECT_DETAIL_OPTIONS);

            // 실제로 메뉴별 옵션 그룹/옵션 값을 DB에서 조회
            List<OptionGroupDto> optionGroups = loadOptionGroups(state.getMenuId());

            ShowDetailOptionsResponse res = new ShowDetailOptionsResponse();
            res.setType("show_detail_options");
            res.setMenuName(state.getMenuName());
            res.setOptionGroups(optionGroups);
            return res;
        } else {
            // 옵션 없이 바로 한 잔 완료
            state.setStep(OrderStep.COMPLETE);
            return completeSingleItemOrder(state);
        }
    }

    public OrderItemCompleteResponse selectDetailOptions(String wsSessionId, List<Integer> selectedOptionValueIds) {

        OrderFlowState state = stateManager.get(wsSessionId);
        if (state == null) {
            throw new IllegalStateException("주문 상태가 존재하지 않습니다. 먼저 order_start를 호출해야 합니다.");
        }

        state.setSelectedOptionValueIds(new ArrayList<>(selectedOptionValueIds));
        state.setStep(OrderStep.COMPLETE);

        return completeSingleItemOrder(state);
    }

    private List<OptionGroupDto> loadOptionGroups(Integer menuId) {

        var groups = optionGroupRepository.findDetailGroupsForMenu(menuId);

        List<OptionGroupDto> result = new ArrayList<>();

        for (var group : groups) {
            // 그룹별 활성 옵션 값 조회
            var values = optionValueRepository
                    .findByOptionGroupIdAndActiveTrueOrderBySortOrder(group.getId());

            OptionGroupDto groupDto = new OptionGroupDto();
            groupDto.setGroupName(group.getDisplayName());
            groupDto.setMaxSelect(group.getMaxSelect());

            List<OptionDto> optionDtos = values.stream()
                    .map(v -> new OptionDto(
                            v.getId(),
                            v.getDisplayName(),
                            v.getExtraPrice().intValue()
                    ))
                    .toList();

            groupDto.setOptions(optionDtos);
            result.add(groupDto);
        }

        return result;
    }

    private OrderItemCompleteResponse completeSingleItemOrder(OrderFlowState state){
        // 1) 저장용 DTO로 변환
        OrderSaveDto orderSaveDto=toOrderSaveDto(state);

        // 2) 로컬 DB에 저장 (헤더/디테일/옵션까지)
        orderSaveService.saveOrder(orderSaveDto);

        // 3) 프론트로 내려줄 완료 메시지 생성
        return buildOrderItemCompleteResponse(state);
    }

    private OrderSaveDto toOrderSaveDto(OrderFlowState state){
        OrderItemSaveDto item = new OrderItemSaveDto();
        item.setMenuId(state.getMenuId());
        item.setMenuName(state.getMenuName());
        item.setQuantity(1);
        item.setTemperature(state.getTemperature());
        item.setSize(state.getSize());

        if (state.getSelectedOptionValueIds() != null) {
            item.setOptionValueIds(new ArrayList<>(state.getSelectedOptionValueIds()));
        } else {
            item.setOptionValueIds(List.of());
        }

        OrderSaveDto dto = new OrderSaveDto();
        dto.setStoreId(Integer.valueOf(state.getStoreId()));
        dto.setUserId(null); // 비회원 주문 (로그인 붙으면 세팅)
        dto.setItems(List.of(item));

        return dto;
    }

    private OrderItemCompleteResponse buildOrderItemCompleteResponse(OrderFlowState state) {

        // 가격 계산 로직(기본 가격 + 옵션 가격)도 여기서 처리 가능
        String msg = String.format(
                "%s %s / %s 사이즈 1잔을 장바구니에 담았습니다. 다른 메뉴도 주문하시겠어요?",
                state.getMenuName(),
                nullToDash(state.getTemperature()),
                nullToDash(state.getSize())
        );

        OrderItemCompleteResponse res = new OrderItemCompleteResponse();
        res.setType("order_item_complete");
        res.setMessage(msg);
        // 나중에 합계 금액, 선택 옵션 텍스트 등 추가 가능
        return res;
    }

    private String nullToDash(String v) {
        return v == null ? "-" : v;
    }
}
