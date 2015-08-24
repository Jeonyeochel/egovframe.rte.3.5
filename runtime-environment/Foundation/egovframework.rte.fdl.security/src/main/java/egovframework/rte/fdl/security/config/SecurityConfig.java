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

import javax.sql.DataSource;

/**
 * egov-security schema namespace 처리를 담당하는 bean 클래스
 * 
 *<p>Desc.: 설정 간소화 처리에 사용되는 bean으로 설정에 대한 정보를 보관</p>
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
public class SecurityConfig {
    public static final String DEF_USERS_BY_USERNAME_QUERY_SQL = "select user_id, password, enabled, users.* from users where user_id = ?";
    public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY_SQL = "select user_id, authority from authorites where user_id = ?";
    
	private String loginUrl;
	private String logoutSuccessUrl;
	private String loginFailureUrl;
	private String accessDeniedUrl;
	
	private DataSource dataSource;
	
	private String jdbcUsersByUsernameQuery = DEF_USERS_BY_USERNAME_QUERY_SQL;
	private String jdbcAuthoritiesByUsernameQuery = DEF_AUTHORITIES_BY_USERNAME_QUERY_SQL;
	
	private String jdbcMapClass = "egovframework.rte.fdl.security.userdetails.DefaultMapUserDetailsMapping";
	
	private String requestMatcherType = "regex";		// regex, ant, ciRegex (case-insensitive) 
	private String hash = "sha-256";							// hashing algorithm
	private boolean hashBase64 = true;						// default = true
	
	private int concurrentMaxSessons;
	private String concurrentExpiredUrl;
	
	private String defaultTargetUrl;

	private boolean supportPointcut;
	
	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutSuccessUrl() {
		return logoutSuccessUrl;
	}

	public void setLogoutSuccessUrl(String logoutSuccessUrl) {
		this.logoutSuccessUrl = logoutSuccessUrl;
	}

	public String getLoginFailureUrl() {
		return loginFailureUrl;
	}

	public void setLoginFailureUrl(String loginFailureUrl) {
		this.loginFailureUrl = loginFailureUrl;
	}

	public String getAccessDeniedUrl() {
		return accessDeniedUrl;
	}

	public void setAccessDeniedUrl(String accessDeniedUrl) {
		this.accessDeniedUrl = accessDeniedUrl;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getJdbcUsersByUsernameQuery() {
		return jdbcUsersByUsernameQuery;
	}

	public void setJdbcUsersByUsernameQuery(String jdbcUsersByUsernameQuery) {
		this.jdbcUsersByUsernameQuery = jdbcUsersByUsernameQuery;
	}

	public String getJdbcAuthoritiesByUsernameQuery() {
		return jdbcAuthoritiesByUsernameQuery;
	}

	public void setJdbcAuthoritiesByUsernameQuery(String jdbcAuthoritiesByUsernameQuery) {
		this.jdbcAuthoritiesByUsernameQuery = jdbcAuthoritiesByUsernameQuery;
	}

	public String getJdbcMapClass() {
		return jdbcMapClass;
	}

	public void setJdbcMapClass(String jdbcMapClass) {
		this.jdbcMapClass = jdbcMapClass;
	}

	public String getRequestMatcherType() {
		return requestMatcherType;
	}

	public void setRequestMatcherType(String requestMatcherType) {
		this.requestMatcherType = requestMatcherType;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public boolean isSupportPointcut() {
		return supportPointcut;
	}

	public void setSupportPointcut(boolean supportPointcut) {
		this.supportPointcut = supportPointcut;
	}

	public int getConcurrentMaxSessons() {
		return concurrentMaxSessons;
	}

	public void setConcurrentMaxSessons(int concurrentMaxSessons) {
		this.concurrentMaxSessons = concurrentMaxSessons;
	}

	public String getConcurrentExpiredUrl() {
		return concurrentExpiredUrl;
	}

	public void setConcurrentExpiredUrl(String concurrentExpiredUrl) {
		this.concurrentExpiredUrl = concurrentExpiredUrl;
	}

	public String getDefaultTargetUrl() {
		return defaultTargetUrl;
	}

	public void setDefaultTargetUrl(String defaultTargetUrl) {
		this.defaultTargetUrl = defaultTargetUrl;
	}

	public boolean isHashBase64() {
		return hashBase64;
	}

	public void setHashBase64(boolean hashBase64) {
		this.hashBase64 = hashBase64;
	}
}
