package com.example.tak.modules.agent.snapshot.service;

import com.example.tak.common.*;
import com.example.tak.modules.agent.snapshot.dto.*;
import com.example.tak.modules.agent.snapshot.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuSnapshotService {
    private final StoreRepository storeRepository;

    private final MenuRepository menuRepository;
    private final MenuOptionGroupRepository menuOptionGroupRepository;
    private final MenuOptionRuleRepository menuOptionRuleRepository;

    private final OptionGroupRepository optionGroupRepository;
    private final OptionValueRepository optionValueRepository;

    @Transactional(readOnly=true)
    public MenuSnapshotDto buildSnapshot(Integer storeId, int menuVersion){

        // 매장 정보 조회
        Store store=storeRepository.findById(storeId)
                .orElseThrow(()->new IllegalArgumentException("Store not found: " + storeId));

        // 해당 매장에서 판매하는 메뉴 목록 조회
        List<Menu> menus = menuRepository.findMenusByStoreId(storeId);

        // Menu -> MenuDto 변환
        List<MenuDto> menuDtos=menus.stream()
                .map(this::buildMenuDto)
                .toList();

        // 최상위 스냅샷 DTO 생성
        return new MenuSnapshotDto(
                store.getId(),
                store.getName(),
                store.getAddress(),
                menuVersion,
                menuDtos
        );
    }

    private MenuDto buildMenuDto(Menu menu){
        // 영양소 DTO
        NutrientDto nutrientDto=new NutrientDto(
                menu.getCalorie(),
                menu.getSugar(),
                menu.getCaffeine()
        );

        // 카테고리/ 태그/ 별칭
        String category = inferCategory(menu);
        List<String> tags = buildTags(menu, category);
        List<String> aliases = buildAliases(menu);

        List<MenuOptionGroup> mappings=menuOptionGroupRepository.findByMenuId(menu.getId());

        List<OptionGroupDto> optionGroupDtos = mappings.stream()
                .map(mog -> buildOptionGroupDto(mog.getGroupId(), menu.getId()))
                .toList();

        return new MenuDto(
                menu.getId(),
                menu.getName(),
                menu.getMenuImage(),
                menu.getPrice(),
                menu.getDescription(),
                nutrientDto,
                menu.getAllergen().name(),

                category,
                tags,
                aliases,

                optionGroupDtos
        );
    }

    private OptionGroupDto buildOptionGroupDto(Integer groupId, Integer menuId){
        OptionGroup og=optionGroupRepository.findById(groupId)
                .orElseThrow(()-> new IllegalStateException("Option group not found: "+ groupId));

        List<OptionValue> values = optionValueRepository.findByOptionGroupId(groupId);
        List<OptionValueDto> valueDtos = values.stream()
                .map(this::buildOptionValueDto)
                .toList();

        List<MenuOptionRule> rules = menuOptionRuleRepository.findByMenuIdAndOptionGroupId(menuId, groupId);

        List<OptionRuleDto> ruleDtos = rules.stream()
                .map(this::buildOptionRuleDto)
                .toList();

        return new OptionGroupDto(
                og.getId(),
                og.getGroupKey(),
                og.getDisplayName(),
                og.getSelectionType().name(),
                og.getMinSelect(),
                og.getMaxSelect(),
                og.getSortOrder(),
                og.getRequired(),
                og.getActive(),
                valueDtos,
                ruleDtos
        );
    }

    private OptionValueDto buildOptionValueDto(OptionValue optionValue){
        Integer groupId = (optionValue.getOptionGroup() != null) ? optionValue.getOptionGroup().getId() : null;

        return new OptionValueDto(
                optionValue.getId(),
                groupId,
                optionValue.getValueKey(),
                optionValue.getDisplayName(),
                optionValue.getExtraPrice(),
                optionValue.getSortOrder(),
                optionValue.getActive()
        );
    }

    private OptionRuleDto buildOptionRuleDto(MenuOptionRule menuOptionRule){
        Integer menuId=(menuOptionRule.getMenu() != null) ? menuOptionRule.getMenu().getId() : null;
        Integer groupId = (menuOptionRule.getOptionGroup() != null) ? menuOptionRule.getOptionGroup().getId() : null;
        Integer valueId = (menuOptionRule.getOptionValue() != null) ? menuOptionRule.getOptionValue().getId() : null;

        return new OptionRuleDto(
                menuOptionRule.getId(),
                menuOptionRule.getRuleType().name(),
                menuOptionRule.getRuleJson(),
                menuId,
                groupId,
                valueId
        );
    }

    private String inferCategory(Menu menu){
        Integer id = menu.getId();
        if(id==null) return "etc";

        int mid = id;
        if (mid >= 1 && mid <= 6) return "coffee";
        if (mid >= 10 && mid <= 15) return "latte";
        if (mid >= 20 && mid <= 24) return "noncoffee_latte";
        if (mid >= 30 && mid <= 33) return "ade";
        if (mid >= 40 && mid <= 43) return "tea";
        if (mid >= 50 && mid <= 52) return "smoothie";
        if (mid >= 60 && mid <= 62) return "frappe";
        if (mid >= 70 && mid <= 72) return "juice";
        return "etc";
    }

    private List<String> buildTags(Menu menu, String category){
        String catTag = "cat:" + category;

        String caffeineTag = (menu.getCaffeine() != null && menu.getCaffeine() > 0)
                ? "caffeinated"
                : "decaf";

        String sweetTag;
        if (menu.getSugar() == null) {
            sweetTag = "unknown_sweetness";
        } else if (menu.getSugar() >= 25) {
            sweetTag = "sweet";
        } else if (menu.getSugar() >= 10) {
            sweetTag = "medium_sweet";
        } else {
            sweetTag = "light_sweet";
        }

        return List.of(catTag, caffeineTag, sweetTag);
    }

    private List<String> buildAliases(Menu menu){
        String name = menu.getName();
        if (name == null) {
            return Collections.emptyList();
        }

        if (name.contains("아메리카노")) {
            return List.of(name, "아아");
        }

        if (name.contains("카페라떼") || name.contains("라떼")) {
            return List.of(name, "라떼");
        }

        // 기본: 자기 이름만
        return List.of(name);
    }

    @SuppressWarnings("unused")
    private int toIntPrice(BigDecimal price){
        if (price==null) return 0;
        return price.intValue();
    }
}
