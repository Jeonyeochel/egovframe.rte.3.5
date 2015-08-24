package egovframework.rte.ptl.mvc.bind;

import java.util.Iterator;
import java.util.Map;

import egovframework.rte.ptl.mvc.bind.annotation.CommandMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SuppressWarnings("deprecation")
@Controller
public class CommandMapTestController {

	@RequestMapping("/test.do")
	public void test(HttpServletRequest request, @CommandMap Map<String, String> commandMap){

		for(Iterator<String> it=commandMap.keySet().iterator();it.hasNext();){
			String key = it.next();
			String value = commandMap.get(key);
			request.setAttribute(key, value);
		}
	}

}
