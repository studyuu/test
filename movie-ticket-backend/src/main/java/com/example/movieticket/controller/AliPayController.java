package com.example.movieticket.controller;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.example.movieticket.config.AliPayConfig;
import com.example.movieticket.entity.Order;
import com.example.movieticket.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/alipay")
public class AliPayController {

    @Autowired
    private AliPayConfig aliPayConfig;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 创建支付宝支付链接
     * 
     * @param orderId 订单号
     * @return 支付链接或错误信息
     */
    @GetMapping("/pay")
    public Map<String, Object> pay(@RequestParam String orderId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "订单不存在");
            response.put("data", null);
            return response;
        }

        Order order = orderOpt.get();
        if (!"pending".equals(order.getStatus())) {
            response.put("code", 400);
            response.put("message", "订单状态不正确");
            response.put("data", null);
            return response;
        }

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    aliPayConfig.getFormat(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType());

            AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
            request.setReturnUrl(aliPayConfig.getReturnUrl());
            request.setNotifyUrl(aliPayConfig.getNotifyUrl());

            String subject = "电影票订单 - " + order.getOrderId();
            String body = "订单详情";

            String bizContent = "{" +
                    "\"out_trade_no\":\"" + order.getOrderId() + "\"," +
                    "\"total_amount\":\"" + order.getTotalPrice().doubleValue() + "\"," +
                    "\"subject\":\"" + subject + "\"," +
                    "\"body\":\"" + body + "\"," +
                    "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"" +
                    "}";

            request.setBizContent(bizContent);

            AlipayTradePagePayResponse alipayResponse = alipayClient.pageExecute(request);

            if (alipayResponse.isSuccess()) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", alipayResponse.getBody());
            } else {
                response.put("code", 500);
                response.put("message", alipayResponse.getMsg());
                response.put("data", null);
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "支付接口调用失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    /**
     * 支付宝异步回调接口
     */
    @PostMapping("/notify")
    public String notify(HttpServletRequest request) {
        try {
            Map<String, String> params = new HashMap<>();
            Map<String, String[]> requestParams = request.getParameterMap();

            for (String name : requestParams.keySet()) {
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                params.put(name, valueStr);
            }

            boolean signVerified = AlipaySignature.rsaCheckV1(
                    params,
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getSignType());

            if (signVerified) {
                String outTradeNo = request.getParameter("out_trade_no");
                String tradeStatus = request.getParameter("trade_status");

                if ("TRADE_SUCCESS".equals(tradeStatus) || "TRADE_FINISHED".equals(tradeStatus)) {
                    Optional<Order> orderOpt = orderRepository.findByOrderId(outTradeNo);
                    if (orderOpt.isPresent()) {
                        Order order = orderOpt.get();
                        order.setStatus("completed");
                        orderRepository.save(order);
                    }
                }
                return "success";
            } else {
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /**
     * 查询订单支付状态
     */
    @GetMapping("/query")
    public Map<String, Object> query(@RequestParam String orderId) {
        Map<String, Object> response = new HashMap<>();

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    aliPayConfig.getFormat(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType());

            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            String bizContent = "{" +
                    "\"out_trade_no\":\"" + orderId + "\"" +
                    "}";
            request.setBizContent(bizContent);

            AlipayTradeQueryResponse alipayResponse = alipayClient.execute(request);

            if (alipayResponse.isSuccess()) {
                Map<String, Object> result = new HashMap<>();
                result.put("tradeNo", alipayResponse.getTradeNo());
                result.put("outTradeNo", alipayResponse.getOutTradeNo());
                result.put("tradeStatus", alipayResponse.getTradeStatus());
                result.put("totalAmount", alipayResponse.getTotalAmount());
                result.put("receiptAmount", alipayResponse.getReceiptAmount());

                response.put("code", 200);
                response.put("message", "success");
                response.put("data", result);
            } else {
                response.put("code", 500);
                response.put("message", alipayResponse.getMsg());
                response.put("data", null);
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund")
    public Map<String, Object> refund(@RequestParam String orderId,
            @RequestParam(required = false) BigDecimal refundAmount) {
        Map<String, Object> response = new HashMap<>();

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "订单不存在");
            response.put("data", null);
            return response;
        }

        Order order = orderOpt.get();
        if (!"completed".equals(order.getStatus())) {
            response.put("code", 400);
            response.put("message", "订单状态不正确，无法退款");
            response.put("data", null);
            return response;
        }

        try {
            AlipayClient alipayClient = new DefaultAlipayClient(
                    aliPayConfig.getGatewayUrl(),
                    aliPayConfig.getAppId(),
                    aliPayConfig.getAppPrivateKey(),
                    aliPayConfig.getFormat(),
                    aliPayConfig.getCharset(),
                    aliPayConfig.getAlipayPublicKey(),
                    aliPayConfig.getSignType());

            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();

            BigDecimal amount = refundAmount != null ? refundAmount : order.getTotalPrice();

            String bizContent = "{" +
                    "\"out_trade_no\":\"" + orderId + "\"," +
                    "\"refund_amount\":\"" + amount.doubleValue() + "\"," +
                    "\"refund_reason\":\"用户申请退款\"" +
                    "}";
            request.setBizContent(bizContent);

            AlipayTradeRefundResponse alipayResponse = alipayClient.execute(request);

            if (alipayResponse.isSuccess()) {
                order.setStatus("cancelled");
                orderRepository.save(order);

                Map<String, Object> result = new HashMap<>();
                result.put("tradeNo", alipayResponse.getTradeNo());
                result.put("outTradeNo", alipayResponse.getOutTradeNo());
                result.put("refundAmount", amount);

                response.put("code", 200);
                response.put("message", "退款成功");
                response.put("data", result);
            } else {
                response.put("code", 500);
                response.put("message", alipayResponse.getMsg());
                response.put("data", null);
            }

        } catch (AlipayApiException e) {
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "退款失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }
}