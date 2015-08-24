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
package egovframework.rte.fdl.security.config.internal;

import egovframework.rte.fdl.security.config.SecurityConfig;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * dataSource 지정을 처리하는 factory bean 클래스
 * 
 *<p>Desc.: 설정 간소화 처리에 사용되는 내부 factory bean</p>
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
public class DataSourceFactoryBean implements FactoryBean<DataSource>, ApplicationContextAware {
	private static final String DEF_DATASOURCE_NAME = "dataSource";

	private ApplicationContext context = null;
	
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}

	public DataSource getObject() throws Exception {
		SecurityConfig config = context.getBean(SecurityConfig.class);
		
		if (config == null) {
			throw new NoSuchBeanDefinitionException("No bean of type [SecurityConfig.class] is defined.");
		}

		if (config.getDataSource() != null) {
			return config.getDataSource();
		} else {
			if (context.containsBean(DEF_DATASOURCE_NAME)) {
				
				return (DataSource) context.getBean(DEF_DATASOURCE_NAME);
			} else {
				throw new NoSuchBeanDefinitionException("No bean of name [dataSource] is defined.");
			}
		}
	}

	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
