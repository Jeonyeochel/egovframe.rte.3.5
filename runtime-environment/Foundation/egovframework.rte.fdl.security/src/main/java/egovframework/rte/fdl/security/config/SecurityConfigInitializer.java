/*
 * Copyright 2014 MOSPA(Ministry of Security and Public Administration).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package egovframework.rte.fdl.security.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.Filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PlaintextPasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.StringUtils;

/**
 * egov-security schema namespace 처리를 담당하는 bean 클래스
 *
 *<p>Desc.: 설정 간소화 처리에 사용되는 bean으로 설정 초기화를 처리</p>
 *
 * @author Vincent Han
 * @since 2014.03.12
 * @version 3.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *
 *   수정일				수정자		수정내용
 *  ---------------------------------------------------------------------------------
 *   2014.03.12	한성곤		Spring Security 설정 간소화 기능 추가
 *
 * </pre>
 */
public class SecurityConfigInitializer implements ApplicationContextAware {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfigInitializer.class);

	private ApplicationContext context;

	private SecurityConfig config;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;

		config = (SecurityConfig) context.getBean(SecurityConfig.class);
	}

	@PostConstruct
	public void init() {
		LOGGER.debug("init() started...");

		if (StringUtils.hasText(config.getAccessDeniedUrl())) {
			LOGGER.debug("Replaced access denied url : {}", config.getAccessDeniedUrl());
			registerAccessDeniedUrl(config.getAccessDeniedUrl());
		}

		if (StringUtils.hasText(config.getLoginUrl())) {
			LOGGER.debug("Replaced login url : {}", config.getLoginUrl());
			registerLoginUrl();
		}

		if (StringUtils.hasText(config.getLoginFailureUrl())) {
			LOGGER.debug("Replaced login failure url : {}", config.getLoginFailureUrl());
			registerLoginFailureUrl(config.getLoginFailureUrl());
		}

		if (StringUtils.hasText(config.getLogoutSuccessUrl())) {
			LOGGER.debug("Replaced logout success url : {}", config.getLogoutSuccessUrl());
			registerLogoutSuccessUrl(config.getLogoutSuccessUrl());
		}

		registerJdbcInfo(config.getJdbcUsersByUsernameQuery(), config.getJdbcAuthoritiesByUsernameQuery(), config.getJdbcMapClass());

		if (StringUtils.hasText(config.getHash())) {
			LOGGER.debug("Password Encoder Algorithm : {}", config.getHash());
			registerHash(config.getHash(), config.isHashBase64());
		}

		if (config.getConcurrentMaxSessons() > 0 || StringUtils.hasText(config.getConcurrentExpiredUrl())) {
			LOGGER.debug("Concurrent max sessions : {}", config.getConcurrentMaxSessons());
			LOGGER.debug("Concurrent concurrent expired url  : {}", config.getConcurrentExpiredUrl());

			registerConcurrentControl(config.getConcurrentMaxSessons(), config.getConcurrentExpiredUrl());
		}

		if (StringUtils.hasText(config.getDefaultTargetUrl())) {
			LOGGER.debug("Default target url : {}", config.getDefaultTargetUrl());
			registerDefaultTargetUrl(config.getDefaultTargetUrl());
		}

		LOGGER.debug("init() ended...");
	}

	private <T extends Filter> T getSecurityFilter(Class<T> type) {

		Map<String, DefaultSecurityFilterChain> filterChainMap = context.getBeansOfType(DefaultSecurityFilterChain.class);

		for (DefaultSecurityFilterChain filterChain : filterChainMap.values()) {
			for (Filter filter : filterChain.getFilters()) {
				if (type.isInstance(filter)) {
					return type.cast(filter);
				}
			}
		}

		//throw new NoSuchBeanDefinitionException("No bean of type [" + type.getName() + "] is defined.");
		throw new NoSuchBeanDefinitionException(type);
	}

	private void registerLogoutSuccessUrl(String logoutSuccessUrl) {
		LogoutFilter filter = getSecurityFilter(LogoutFilter.class);

		checkUrl(logoutSuccessUrl);

		try {
			Field field = filter.getClass().getDeclaredField("logoutSuccessHandler");

			field.setAccessible(true);

			SimpleUrlLogoutSuccessHandler logoutSuccessHandler = (SimpleUrlLogoutSuccessHandler) field.get(filter);

			logoutSuccessHandler.setDefaultTargetUrl(logoutSuccessUrl);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private void registerLoginFailureUrl(String loginFailureUrl) {
		UsernamePasswordAuthenticationFilter filter = getSecurityFilter(UsernamePasswordAuthenticationFilter.class);

		checkUrl(loginFailureUrl);

		SimpleUrlAuthenticationFailureHandler failureHandler = null;

		Method method = null;

		try {
			//method = UsernamePasswordAuthenticationFilter.class.getDeclaredMethod("getFailureHandler", (Class<?>[]) null);
			method = AbstractAuthenticationProcessingFilter.class.getDeclaredMethod("getFailureHandler", (Class<?>[]) null);

			method.setAccessible(true);

			failureHandler = (SimpleUrlAuthenticationFailureHandler) method.invoke(filter, (Object[]) null);
		} catch (Exception ex) {
			LOGGER.error("## registerLoginFailureUrl : {}", ex);
			throw new RuntimeException(ex);
		}

		failureHandler.setDefaultFailureUrl(loginFailureUrl);

	}

	private void registerLoginUrl() {
		/*
		ExceptionTranslationFilter filter = getSecurityFilter(ExceptionTranslationFilter.class);

		checkUrl(loginUrl);

		LoginUrlAuthenticationEntryPoint entryPoint = (LoginUrlAuthenticationEntryPoint) filter.getAuthenticationEntryPoint();

		entryPoint.setLoginFormUrl(loginUrl);
		*/

		// LoginUrlAuthenticationEntryPoint 설정을 통해 지정함 (LoginFormUrlFactoryBean 참조)
	}

	protected void registerAccessDeniedUrl(String accessDeniedUrl) {

		ExceptionTranslationFilter filter = getSecurityFilter(ExceptionTranslationFilter.class);

		checkUrl(accessDeniedUrl);

		AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
		accessDeniedHandler.setErrorPage(accessDeniedUrl);

		filter.setAccessDeniedHandler(accessDeniedHandler);
	}

	protected void registerJdbcInfo(String jdbcUsersByUsernameQuery, String jdbcAuthoritiesByUsernameQuery, String jdbcMapClass) {
		/*
		EgovJdbcUserDetailsManager manager = (EgovJdbcUserDetailsManager)context.getBean(EgovJdbcUserDetailsManager.class);

		//manager.setDataSource(dataSource);		// set by DataSourceFactorybBean
		manager.setUsersByUsernameQuery(jdbcUsersByUsernameQuery);
		manager.setAuthoritiesByUsernameQuery(jdbcAuthoritiesByUsernameQuery);
		manager.setMapClass(jdbcMapClass);
		*/

		// FactoryBean을 통해 지정
	}

	protected void registerHash(String hash, boolean isHashBase64) {
		DaoAuthenticationProvider authentication = context.getBean(DaoAuthenticationProvider.class);

		if (hash.equalsIgnoreCase("plaintext")) {

			authentication.setPasswordEncoder(new PlaintextPasswordEncoder());

		} else if (hash.equalsIgnoreCase("md5")) {

			Md5PasswordEncoder password = new Md5PasswordEncoder();
			password.setEncodeHashAsBase64(isHashBase64);
			authentication.setPasswordEncoder(password);

		} else if (hash.equalsIgnoreCase("sha")) {

			ShaPasswordEncoder password = new ShaPasswordEncoder();
			password.setEncodeHashAsBase64(isHashBase64);
			authentication.setPasswordEncoder(password);

		} else if (hash.equalsIgnoreCase("sha-256")) {
			// CHECKSTYLE:OFF
			ShaPasswordEncoder password = new ShaPasswordEncoder(256);
			// CHECKSTYLE:ON
			password.setEncodeHashAsBase64(isHashBase64);
			authentication.setPasswordEncoder(password);

		} else if (hash.equalsIgnoreCase("bcrypt")) {

			BCryptPasswordEncoder password = new BCryptPasswordEncoder();

			authentication.setPasswordEncoder(password);

		} else {

			throw new IllegalArgumentException("'hash' attribute have to be plaintext, md5, sha, sha-256, or bcrypt");

		}
	}

	private void registerConcurrentControl(int concurrentMaxSessons, String concurrentExpiredUrl) {
		// concurrentMaxSessons and concurrentExpiredURL are set by config (<session-management> and ConcurrentSessionFilter)
	}

	private void registerDefaultTargetUrl(String defaultTargetUrl) {
		//UsernamePasswordAuthenticationFilter
		AbstractAuthenticationProcessingFilter filter = getSecurityFilter(AbstractAuthenticationProcessingFilter.class);

		checkUrl(defaultTargetUrl);

		Method method = null;
		try {
			method = AbstractAuthenticationProcessingFilter.class.getDeclaredMethod("getSuccessHandler", (Class<?>[]) null);

			method.setAccessible(true);

			SavedRequestAwareAuthenticationSuccessHandler successHandler = (SavedRequestAwareAuthenticationSuccessHandler) method.invoke(filter, (Object[]) null);

			successHandler.setAlwaysUseDefaultTargetUrl(true);
			successHandler.setDefaultTargetUrl(defaultTargetUrl);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	private void checkUrl(String url) {
		if (!UrlUtils.isValidRedirectUrl(url)) {
			LOGGER.warn("Url ({} is not a valid redirect URL (must start with '/' or http(s))", url);
		}

	}
}
