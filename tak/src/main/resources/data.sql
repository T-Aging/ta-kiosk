-- ===== 초기화 (개발용) =====
-- 이미 데이터가 있다면 깔끔하게 지우고 시작
DELETE FROM order_option;
DELETE FROM order_detail;
DELETE FROM order_header;
DELETE FROM menu_option_rule;
DELETE FROM menu_option_group;
DELETE FROM store_menu_mapping;
DELETE FROM option_value;
DELETE FROM option_group;
DELETE FROM menu;
DELETE FROM store;

-- ===== 매장 정보 =====
INSERT INTO store (store_id, store_name, store_address)
VALUES
  (1, '타 카페 1호점', '서울 어딘가 1층');

-- ===== 메뉴 목록 =====
-- menu_id, menu_name, menu_price, description, calorie, sugar, caffeine, allergic, menu_image
-- ✅ 아메리카노는 한 줄만 두고, HOT/ICE는 temperature 옵션으로만 구분

INSERT INTO menu (menu_id, menu_name, menu_price, description, calorie, sugar, caffeine, allergic, menu_image)
VALUES
  -- 아메리카노 계열
  (1,  '아메리카노',           2000, '기본 에스프레소 커피',                     5,   0,   120, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),
  (3,  '연유 아메리카노',      2500, '연유가 들어가 달달한 아메리카노',          80,  15,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),
  (4,  '디카페인 아메리카노',  2500, '카페인을 줄인 아메리카노',                 5,   0,    5, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-americano.jpg'),

  (5,  '콜드브루',             3000, '진하게 우려낸 콜드브루 커피',              10,  0,   150, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-americano.jpg'),
  (6,  '디카페인 콜드브루',    3500, '카페인을 줄인 콜드브루',                   10,  0,    5, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-americano.jpg'),

  -- 라떼 계열
  (10, '카페라떼',             2800, '우유가 들어간 부드러운 라떼',              180, 10,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (11, '디카페인 라떼',        3300, '카페인을 줄인 부드러운 라떼',              180, 10,    5, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (12, '바닐라라떼',           3200, '바닐라 시럽이 들어간 달콤한 라떼',         220, 25,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (13, '카라멜라떼',           3300, '카라멜 시럽이 들어간 달콤한 라떼',         230, 26,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (14, '헤이즐넛라떼',         3200, '헤이즐넛 향이 나는 고소한 라떼',           220, 20,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (15, '카페모카',             3200, '초콜릿이 들어간 달콤한 모카 라떼',         250, 28,  120, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),

  -- 논커피 라떼 / 기타 라떼
  (20, '초코라떼',             3000, '달콤한 초콜릿이 들어간 논커피 라떼',       260, 30,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/choco.jpg'),
  (21, '화이트초코라떼',       3200, '화이트 초콜릿이 들어간 부드러운 라떼',     270, 32,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-latte.jpg'),
  (22, '녹차라떼',             3200, '녹차와 우유가 어우러진 라떼',              240, 20,   30, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/green.jpg'),
  (23, '고구마라떼',           3500, '고구마가 들어간 포만감 있는 라떼',         280, 35,   10, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/hot-latte.jpg'),
  (24, '밀크티라떼',           3300, '홍차와 우유가 들어간 밀크티 라떼',          230, 25,   40, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/ice-latte.jpg'),

  -- 에이드
  (30, '레몬에이드',           3000, '상큼한 레몬이 들어간 에이드',              140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/lemon.jpg'),
  (31, '자몽에이드',           3500, '자몽이 들어간 상큼한 에이드',              150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grapefruit.jpg'),
  (32, '청포도에이드',         3300, '청포도가 들어간 달콤한 에이드',            150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grape.jpg'),
  (33, '오렌지에이드',         3300, '오렌지가 들어간 상큼한 에이드',            150, 32,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/orange-ade.jpg'),

  -- 티
  (40, '얼그레이티',           2500, '향긋한 얼그레이 홍차',                     5,   0,   30, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (41, '캐모마일티',           2500, '은은한 꽃향의 캐모마일 티',                5,   0,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (42, '페퍼민트티',           2500, '상쾌한 페퍼민트 허브티',                   5,   0,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),
  (43, '복숭아 아이스티',      2500, '달달한 복숭아 향의 아이스티',              120, 28,   0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/tea/tea.jpg'),

  -- 스무디
  (50, '딸기 스무디',          3800, '딸기가 들어간 달콤한 스무디',              250, 35,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/berry.jpg'),
  (51, '망고 스무디',          3800, '망고가 들어간 달콤한 스무디',              250, 35,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/mango.jpg'),
  (52, '요거트 스무디',        3800, '상큼한 요거트 베이스 스무디',              230, 30,    0, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/yogurt.jpg'),

  -- 프라페
  (60, '모카프라페',           4200, '모카 베이스의 달콤한 프라페',              300, 40,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/coffee/coffee.jpg'),
  (61, '카라멜프라페',         4300, '카라멜 소스가 듬뿍 들어간 프라페',         310, 42,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/caramel.jpg'),
  (62, '쿠키앤크림프라페',     4500, '쿠키와 크림이 들어간 프라페',              320, 45,   80, 'MILK', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/non-coffee/cookie.jpg'),

  -- 주스
  (70, '오렌지 주스',          3500, '상큼한 오렌지 생과일 주스',                140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/orange-juice.jpg'),
  (71, '포도 주스',            3500, '달콤한 포도 생과일 주스',                  140, 30,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/grape.jpg'),
  (72, '토마토 주스',          3500, '담백한 토마토 주스',                       80,  15,    0, 'NONE', 'https://inha-capstone-23-taging-kiosk.s3.us-west-2.amazonaws.com/fruit/tomato.jpg');

-- ===== 매장-메뉴 매핑 =====
INSERT INTO store_menu_mapping (store_id, menu_id)
SELECT 1, menu_id FROM menu;

-- ===== 옵션 그룹 정의 =====
-- group_id, group_key, display_name, selection_type, min_select, max_select, sort_order, is_required, is_active
-- ✅ group_key를 코드에서 쓰는 값과 맞춰서 소문자 'size', 'temperature' 사용

INSERT INTO option_group (group_id, group_key, display_name, selection_type, min_select, max_select, sort_order, is_required, is_active)
VALUES
  (1, 'size',        '사이즈 선택',   'single', 1, 1, 1, 1, 1),
  (2, 'temperature', 'HOT/ICE 선택',  'single', 1, 1, 2, 1, 1),
  (3, 'shot',        '샷 추가',       'multi',  0, 3, 3, 0, 1),
  (4, 'syrup',       '시럽 선택',     'multi',  0, 3, 4, 0, 1),
  (5, 'milk_type',   '우유 선택',     'single', 0, 1, 5, 0, 1);

-- ===== 옵션 값 정의 =====
-- value_id, group_id, value_key, display_name, extra_price, sort_order, is_active
-- ✅ SIZE: SMALL / REGULAR / LARGE 하나씩 정의
INSERT INTO option_value (value_id, group_id, value_key, display_name, extra_price, sort_order, is_active)
VALUES
  -- SIZE
  (1, 1, 'SMALL',   '스몰',          0,    1, 1),
  (2, 1, 'REGULAR', '레귤러',        500,  2, 1),
  (3, 1, 'LARGE',   '라지',          1000, 3, 1),

  -- TEMPERATURE
  (4, 2, 'HOT',     'HOT',           0,    1, 1),
  (5, 2, 'ICE',     'ICE',           0,    2, 1),

  -- SHOT
  (6, 3, 'SHOT1',   '샷 추가',   500, 1, 1),
  (7, 3, 'SHOT2',   '샷 추가 2번',   1000,2, 1),

  -- SYRUP
  (8, 4, 'VANILLA', '바닐라 시럽',   500, 1, 1),
  (9, 4, 'CARAMEL', '카라멜 시럽',   500, 2, 1),
  (10,4, 'HAZELNUT','헤이즐넛 시럽', 500, 3, 1),

  -- MILK_TYPE
  (11,5, 'WHOLE',   '일반 우유',     0,   1, 1),
  (12,5, 'LOWFAT',  '저지방 우유',   0,   2, 1),
  (13,5, 'OAT',     '오트 밀크',     500, 3, 1);

-- ===== 메뉴-옵션 그룹 매핑 =====
-- ✅ 모든 메뉴에 SIZE 그룹(1) 부여
INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 1
FROM menu;

-- ✅ temperature가 필요한 모든 메뉴에 TEMP 그룹(2) 부여 (여기서는 전 메뉴에 부여)
INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 2
FROM menu;

-- 아메리카노/콜드브루, 라떼, 논커피 라떼는 SHOT/SYRUP/MILK_TYPE 일부 사용

-- 아메리카노/콜드브루 (1,3,4,5,6): shot, syrup
INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 3
FROM menu
WHERE menu_id IN (1,3,4,5,6);

INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 4
FROM menu
WHERE menu_id IN (1,3,4,5,6);

-- 라떼 계열 (10 ~ 15): shot, syrup, milk_type
INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 3
FROM menu
WHERE menu_id BETWEEN 10 AND 15;

INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 4
FROM menu
WHERE menu_id BETWEEN 10 AND 15;

INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 5
FROM menu
WHERE menu_id BETWEEN 10 AND 15;

-- 논커피 라떼 (20 ~ 24): milk_type 만
INSERT INTO menu_option_group (menu_id, group_id)
SELECT menu_id, 5
FROM menu
WHERE menu_id BETWEEN 20 AND 24;

-- ===== 메뉴 옵션 룰 (기본값/추천/금지 등) =====
-- rule_type('default','forbid','recommend'), rule_json(JSON), menu_id, group_id, value_id

-- 공통: SIZE 기본값 REGULAR (모든 메뉴)
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"size","valueKey":"REGULAR"}', menu_id, 1, 2
FROM menu;

-- 1) 아메리카노 / 콜드브루 (1,3,4,5,6)
--   - 둘 다 가능한 애(1,3,4): default ICE, 둘 다 허용
--   - ICE only 애(5,6): default ICE + HOT forbid

-- 아메리카노/연유/디카페인 아메 (둘 다 가능)
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id IN (1,3,4);

-- 콜드브루/디카페인 콜드브루 (ICE only)
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id IN (5,6);

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id IN (5,6);

-- 2) 라떼 계열 (10 ~ 15) : 둘 다 가능, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id BETWEEN 10 AND 15;

-- 라떼: 샷 1샷 추가 recommend
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'recommend', '{"groupKey":"shot","valueKey":"SHOT1"}', menu_id, 3, 6
FROM menu WHERE menu_id BETWEEN 10 AND 15;

-- 3) 논커피 라떼 (20 ~ 24) : 둘 다 가능, 기본 HOT
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id BETWEEN 20 AND 24;

-- 논커피 라떼: milk_type 기본 WHOLE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"milk_type","valueKey":"WHOLE"}', menu_id, 5, 11
FROM menu WHERE menu_id BETWEEN 20 AND 24;

-- 4) 에이드 (30 ~ 33) : ICE only, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id BETWEEN 30 AND 33;

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id BETWEEN 30 AND 33;

-- 5) 티 (40,41,42 HOT only / 43 ICE only)

-- 40~42: HOT only, 기본 HOT
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id IN (40,41,42);

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id IN (40,41,42);

-- 43: 아이스티 → ICE only, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
VALUES ('default', '{"groupKey":"temperature","valueKey":"ICE"}', 43, 2, 5);

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
VALUES ('forbid', '{"groupKey":"temperature","valueKey":"HOT"}', 43, 2, 4);

-- 6) 스무디 (50 ~ 52): ICE only, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id BETWEEN 50 AND 52;

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id BETWEEN 50 AND 52;

-- 7) 프라페 (60 ~ 62): ICE only, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id BETWEEN 60 AND 62;

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id BETWEEN 60 AND 62;

-- 8) 주스 (70 ~ 72): ICE only, 기본 ICE
INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'default', '{"groupKey":"temperature","valueKey":"ICE"}', menu_id, 2, 5
FROM menu WHERE menu_id BETWEEN 70 AND 72;

INSERT INTO menu_option_rule (rule_type, rule_json, menu_id, group_id, value_id)
SELECT 'forbid', '{"groupKey":"temperature","valueKey":"HOT"}', menu_id, 2, 4
FROM menu WHERE menu_id BETWEEN 70 AND 72;
