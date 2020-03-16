package org.dreamlife.hippocampus.flowlimit;

import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.web.interceptor.AbstractFlowLimiterInterceptor;
import com.ptc.board.flowlimit.web.response.LimitResponseHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 对accessId进行限流
 *
 * @author baihua
 * @date 2019/3/1
 */
@Slf4j
public class ApiFlowLimiterInterceptor extends AbstractFlowLimiterInterceptor {
    public ApiFlowLimiterInterceptor(FlowLimiterService limiterService, LimitResponseHandler responseHandler) {
        super(limiterService, responseHandler);
    }

    /**
     * 限流判断的标识
     *
     * @param request
     * @param handler
     * @return
     */
    @Override
    public String generateBizKey(HttpServletRequest request, Object handler) {
        return "common";
    }

}
