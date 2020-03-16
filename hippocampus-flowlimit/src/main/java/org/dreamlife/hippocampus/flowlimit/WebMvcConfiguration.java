package org.dreamlife.hippocampus.flowlimit;

import com.ptc.board.flowlimit.FlowLimiterService;
import com.ptc.board.flowlimit.config.FlowLimiterConfig;
import com.ptc.board.flowlimit.web.response.LimitResponseHandler;
import com.ptc.board.flowlimit.web.response.impl.DefaultResponseHandlerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
	@Autowired
    FlowLimiterConfig flowLimiterConfig;
	@Autowired
    FlowLimiterService flowLimiterService;
	/**
	 *  方法级别的单个参数验证开启    
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		try {
			//限流
			registry.addInterceptor(flowLimitInterceptor()).addPathPatterns("/**");
		} catch (Exception e) {
			log.error("init flowLimit interceptor error.", e);
			throw new RuntimeException(e);
		}
		super.addInterceptors(registry);
	}

	/**
	 * 限流拦截器初始化
	 *
	 * @return
	 * @throws Exception
	 */
	public HandlerInterceptor flowLimitInterceptor() throws Exception {

		LimitResponseHandler responseHandler = new DefaultResponseHandlerFactory().create(flowLimiterConfig.getLimitResponse());

		HandlerInterceptor interceptor = new ApiFlowLimiterInterceptor(flowLimiterService, responseHandler);

		return interceptor;
	}
}
