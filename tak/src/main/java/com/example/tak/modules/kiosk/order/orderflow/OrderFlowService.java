package com.example.tak.modules.kiosk.order.orderflow;

import com.example.tak.common.Menu;
import com.example.tak.modules.agent.snapshot.repository.MenuRepository;
import com.example.tak.modules.kiosk.order.dto.tofe.OptionDto;
import com.example.tak.modules.kiosk.order.dto.tofe.OptionGroupDto;
import com.example.tak.modules.kiosk.order.dto.response.*;
import com.example.tak.modules.kiosk.order.repository.OptionGroupRepository;
import com.example.tak.modules.kiosk.order.repository.OptionValueRepository;
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

        // 임시로 둘 다 가능하다고 가정
        boolean hotAvailable=true;
        boolean iceAvailable=true;

        AskTemperatureResponse res= new AskTemperatureResponse();
        res.setType("ask_temperature");
        res.setMenuName(menu.getName());

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

        // TODO: 실제로는 메뉴별 사이즈 가능 여부를 DB에서 조회
        // ex) List<String> sizes = menuOptionRepository.findSizesByMenuId(state.getMenuId());
        List<String> sizes = List.of("R", "L"); // 임시 예시

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
            return buildOrderItemCompleteResponse(state);
        }
    }

    public OrderItemCompleteResponse selectDetailOptions(String wsSessionId, List<Integer> selectedOptionValueIds) {

        OrderFlowState state = stateManager.get(wsSessionId);
        if (state == null) {
            throw new IllegalStateException("주문 상태가 존재하지 않습니다. 먼저 order_start를 호출해야 합니다.");
        }

        state.setSelectedOptionValueIds(new ArrayList<>(selectedOptionValueIds));
        state.setStep(OrderStep.COMPLETE);

        // 여기서 CartService.addItem(...) 같은 걸 호출해도 좋음
        return buildOrderItemCompleteResponse(state);
    }

    private List<OptionGroupDto> loadOptionGroups(Integer menuId) {

        // 실제 구현에서는 menuId 기준으로 option_group / option_value 테이블 조인해서 조회

        // 더미 예시
        List<OptionGroupDto> groups = new ArrayList<>();

        OptionGroupDto shotGroup = new OptionGroupDto();
        shotGroup.setGroupName("샷 추가");
        shotGroup.setMaxSelect(3);
        shotGroup.setOptions(List.of(
                new OptionDto(101, "1샷 추가", 500),
                new OptionDto(102, "2샷 추가", 1000)
        ));

        OptionGroupDto whipGroup = new OptionGroupDto();
        whipGroup.setGroupName("휘핑");
        whipGroup.setMaxSelect(1);
        whipGroup.setOptions(List.of(
                new OptionDto(201, "휘핑 추가", 500)
        ));

        groups.add(shotGroup);
        groups.add(whipGroup);

        return groups;
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
