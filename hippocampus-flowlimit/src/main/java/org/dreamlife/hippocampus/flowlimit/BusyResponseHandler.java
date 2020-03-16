package org.dreamlife.hippocampus.flowlimit;

import com.ptc.board.flowlimit.web.response.LimitResponseHandler;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 限流时获取不到token时响应
 *
 * @author baihua
 * @date 2018/7/11
 */
@Slf4j
public class BusyResponseHandler implements LimitResponseHandler {
    private static final String SYSTEM_BUSY_WORD = "{\"code\":\"system.busy\",\"status\":513}";

    @Override
    public void init(Map<String, Object> params) throws Exception {
    }

    @Override
    public void responseTo(HttpServletRequest request, HttpServletResponse response, String bizKey) throws Exception {
        log.error("system busy");
        response.setStatus(513);
        response.getWriter().print(SYSTEM_BUSY_WORD);
    }
}
