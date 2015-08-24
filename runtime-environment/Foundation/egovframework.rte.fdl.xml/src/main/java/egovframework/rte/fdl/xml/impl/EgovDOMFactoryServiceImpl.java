/*
 * Copyright 2009-2014 MOSPA(Ministry of Security and Public Administration).

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
package egovframework.rte.fdl.xml.impl;

import egovframework.rte.fdl.xml.EgovAbstractXMLFactoryService;
import egovframework.rte.fdl.xml.EgovDOMValidatorService;
import egovframework.rte.fdl.xml.EgovSAXValidatorService;
import egovframework.rte.fdl.xml.exception.UnsupportedException;

/**
 * DOMValidator 생성 Factory Class
 * 
 * @author 개발프레임웍크 실행환경 개발팀 김종호
 * @since 2009.03.18
 * @version 1.0
 * @see <pre>
 *  == 개정이력(Modification Information) ==
 *
 *   수정일      수정자              수정내용
 *  ---------   ---------   -------------------------------
 * 2009.03.18    김종호        최초생성
 *  2015.02.10    Vincent Han	클래스 명명 규칙 수정 (EgovConcreteDOMFactory -> EgovDOMFactoryServiceImpl)
 *
 * </pre>
 */
public class EgovDOMFactoryServiceImpl extends EgovAbstractXMLFactoryService {
	/** DOMValidator  **/
	private EgovDOMValidatorService domvalidator = null;

	/**
	 * DOMValidatorService 생성자
	 * 
	 * @return EgovDOMValidatorService - DOMValidator
	 */
	@Override
	public EgovDOMValidatorService createDOMValidator() {
		domvalidator = new EgovDOMValidatorService();
		return domvalidator;
	}

	/**
	 * EgovSAXValidatorService 생성자
	 * 
	 * @return EgovSAXValidatorService - SAXValidator
	 * @exception UnsupportedException
	 */
	@Override
	public EgovSAXValidatorService createSAXValidator() {
		try {
			throw new UnsupportedException("Unsupported type");
		} catch (UnsupportedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
