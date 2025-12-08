package com.example.tak.modules.kiosk.websocket.handler;

import com.example.tak.modules.agent.service.AgentService;
import com.example.tak.modules.kiosk.cart.dto.request.DeleteCartItemRequest;
import com.example.tak.modules.kiosk.cart.service.CartCommandService;
import com.example.tak.modules.kiosk.cart.service.CartQueryService;
import com.example.tak.modules.kiosk.order.service.ConfirmCommandService;
import com.example.tak.modules.kiosk.recent.service.RecentOrderService;
import com.example.tak.modules.kiosk.start.dto.ConverseRequest;
import com.example.tak.modules.kiosk.start.dto.SessionStartRequest;
import com.example.tak.modules.kiosk.order.dto.request.*;
import com.example.tak.modules.kiosk.order.orderflow.OrderFlowService;
import com.example.tak.modules.kiosk.websocket.dto.WebSocketErrorResponse;
import com.example.tak.modules.kiosk.websocket.session.AgentSessionInfo;
import com.example.tak.modules.kiosk.websocket.session.WebSocketSessionManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageRouter {

    private final ObjectMapper objectMapper;
    private final AgentService agentService;
    private final WebSocketSessionManager webSocketSessionManager;
    private final OrderFlowService orderFlowService;
    private final CartQueryService cartQueryService;
    private final CartCommandService cartCommandService;
    private final ConfirmCommandService confirmOrder;
    private final RecentOrderService recentOrderService;

    public String route(String type, JsonNode data, String wsSessionId) throws Exception {
        return switch (type) {
            // 새로운 대화 세션 시작 요청
            // storeId/menuVersion 기반으로 FastAPI에 warmup 요청
            // 응답으로 받은 agentSessionId와 WebSocket 세션을 매핑 저장
            case "start" -> {
                log.info("[Router] handling START");

                // 파라미터 DTO 구성
                SessionStartRequest request = new SessionStartRequest();
                request.setStoreId(data.get("storeId").asText());
                request.setMenuVersion(data.get("menuVersion").asInt());

                // FastAPI 호출
                var response = agentService.startSession(request);
                String agentSessionId = response.getSessionId();

                // WebSocketSessionId → AgentSessionInfo 매핑 저장
                webSocketSessionManager.register(
                        wsSessionId,
                        AgentSessionInfo.of(request.getStoreId(), request.getMenuVersion(), agentSessionId)
                );

                // ***** 원래는 "webSocketSessionId"를 추가해서 넣는게 맞지만 프론트 dto 수정해야 해서 일단 이렇게 넣음 *****
                response.setSessionId(wsSessionId);

                // 응답 그대로 WebSocket으로 내려보냄
                yield objectMapper.writeValueAsString(response);
            }

            // 기존 대화 세션을 사용하여 AI와 대화를 이어가는 요청
            // sessionId는 클라이언트가 보내지 않아도, WebSocketSessionManager에서 자동으로 찾아서 사용함
            case "converse" -> {
                log.info("[Router] handling CONVERSE, wsSessionId={}", wsSessionId);
                // WebSocketSessionManager에서 세션 정보 가져오기
                // "sessionId"를 data에서 꺼내는 대신 매니저에서 자동으로 가져올 수 있음.
                AgentSessionInfo info=webSocketSessionManager.get(wsSessionId);

                // 세션이 존재하지 않을 경우(예: 서버 재시작/클라이언트의 잘못된 호출)
                if(info==null){
                    log.warn("[Router] no session found for wsSessionId={}", wsSessionId);
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of("SESSION_NOT_FOUND", "세션이 없음. 다시 시작 권장")
                    );
                }

                info.touch();; // 마지막 업데이트 시간 갱신

                log.info("[Router] handling CONVERSE");

                // FastAPI에 보낼 DTO 구성
                ConverseRequest request = new ConverseRequest();
                request.setStoreId(info.getStoreId());
                request.setMenuVersion(info.getMenuVersion());
                request.setSessionId(info.getAgentSessionId());
                request.setUserText(data.get("userText").asText());

                // FastAPI 대화 요청
                var response = agentService.converse(request);

                // WebSocket 전송
                yield objectMapper.writeValueAsString(response);
            }

            // ---------------------------- 회원 최근 주문 ----------------------------
            case "recent_orders" -> {
                log.info("[Router] handling RECENT_ORDERS, wsSessionId={}", wsSessionId);
                var result=recentOrderService.getRecentOrders(wsSessionId);
                yield objectMapper.writeValueAsString(result);
            }

            // ---------------------------- 회원 최근 주문 (상세 조회) ----------------------------
            case "recent_order_detail" -> {
                log.info("[Router] handling RECENT_ORDER_DETAIL, wsSessionId={}", wsSessionId);
                int orderId = data.get("orderId").asInt();

                var result=recentOrderService.getRecentOrderDetail(wsSessionId, orderId);

                yield objectMapper.writeValueAsString(result);
            }

            // ---------------------------- 장바구니 조회 ----------------------------
            case "get_cart" -> {
                log.info("[Router] handling GET_CART, wsSessionId={}", wsSessionId);

                AgentSessionInfo info = webSocketSessionManager.get(wsSessionId);
                if (info == null) {
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of(
                                    "SESSION_NOT_FOUND",
                                    "세션이 없음. 다시 시작 권장"
                            )
                    );
                }

                // storeId는 AgentSessionInfo에 String으로 들어있을 가능성 크니까 변환
                Integer storeId = Integer.valueOf(info.getStoreId());
                String agentSessionId = info.getAgentSessionId();

                var cart = cartQueryService.getCart(storeId, agentSessionId);

                // CartResponseDto 그대로 내려보내기
                yield objectMapper.writeValueAsString(cart);
            }

            // ---------------------------- 장바구니 요소 삭제 ----------------------------
            case "delete_cart_item" -> {
                log.info("[Router] handling DELETE_CART_ITEM, wsSessionId={}", wsSessionId);

                AgentSessionInfo info = webSocketSessionManager.get(wsSessionId);
                if (info == null) {
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of("SESSION_NOT_FOUND", "세션이 없음. 다시 시작 권장")
                    );
                }

                DeleteCartItemRequest req = objectMapper.treeToValue(data, DeleteCartItemRequest.class);

                var res = cartCommandService.deleteCartItem(
                        Integer.valueOf(info.getStoreId()),
                        info.getAgentSessionId(),         // sessionId = agentSessionId
                        req.getOrderDetailId()
                );

                yield objectMapper.writeValueAsString(res);
            }

            // ----------------------------주문 플로우 시작----------------------------
            // 1) 메뉴 선택 시
            case "order_start" ->{
                log.info("[Router] handling ORDER_START, wsSessionId={}", wsSessionId);

                AgentSessionInfo info=webSocketSessionManager.get(wsSessionId);
                if(info==null){
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of("SESSION_NOT_FOUND", "세션이 없음. 다시 시작 권장")
                    );
                }

                OrderStartRequest req=objectMapper.treeToValue(data, OrderStartRequest.class);
                String storeId=info.getStoreId();
                String agentSessionId = info.getAgentSessionId();

                var res = orderFlowService.startOrder(wsSessionId, agentSessionId, storeId, req.getMenuName());

                yield objectMapper.writeValueAsString(res);
            }

            // 2) 핫/ 아이스 선택
            case "select_temperature" ->{
                log.info("[Router] handling SELECT_TEMPERATURE, wsSessionId={}", wsSessionId);

                SelectTemperatureRequest req = objectMapper.treeToValue(data, SelectTemperatureRequest.class);
                var res = orderFlowService.selectTemperature(wsSessionId, req.getTemperature());

                yield objectMapper.writeValueAsString(res);
            }

            // 3) 사이즈 선택
            case "select_size" -> {
                log.info("[Router] handling SELECT_SIZE, wsSessionId={}", wsSessionId);

                SelectSizeRequest req = objectMapper.treeToValue(data, SelectSizeRequest.class);
                var res = orderFlowService.selectSize(wsSessionId, req.getSize());

                yield objectMapper.writeValueAsString(res);
            }

            // 4) 세부 옵션 Y/N
            case "detail_option_yn" ->{
                log.info("[Router] handling DETAIL_OPTION_YN, wsSessionId={}", wsSessionId);

                DetailOptionYnRequest req = objectMapper.treeToValue(data, DetailOptionYnRequest.class);
                var res = orderFlowService.answerDetailOptionYn(wsSessionId, req.getAnswer());

                yield objectMapper.writeValueAsString(res);
            }

            // 5) 세부 옵션 Y: 실제 선택
            case "select_detail_options" -> {
                log.info("[Router] handling SELECT_DETAIL_OPTIONS, wsSessionId={}", wsSessionId);

                SelectDetailOptionsRequest req = objectMapper.treeToValue(data, SelectDetailOptionsRequest.class);
                var res = orderFlowService.selectDetailOptions(wsSessionId, req.getSelectedOptionValueIds());

                yield objectMapper.writeValueAsString(res);
            }

            // ---------------------------- 주문 확정 ----------------------------
            case "order_confirm" -> {
                log.info("[Router] handling ORDER_CONFIRM, wsSessionId={}", wsSessionId);

                AgentSessionInfo info = webSocketSessionManager.get(wsSessionId);
                if (info == null) {
                    yield objectMapper.writeValueAsString(
                            WebSocketErrorResponse.of("SESSION_NOT_FOUND", "세션이 없음. 다시 시작 권장")
                    );
                }

                Integer storeId = Integer.valueOf(info.getStoreId());
                String sessionId = info.getAgentSessionId();
                Integer userId = info.getUserId();

                var res = confirmOrder.confirmOrder(storeId, sessionId, userId);

                yield objectMapper.writeValueAsString(res);
            }

            // 그 외의 타입은 모두 에러 처리
            default -> {
                log.warn("[Router] UNKNOWN type: {}", type);
                WebSocketErrorResponse error=WebSocketErrorResponse.of("UNKNOWN_TYPE","지원하지 않은 메시지 타입");
                yield objectMapper.writeValueAsString(error);
            }
        };

    }
}
