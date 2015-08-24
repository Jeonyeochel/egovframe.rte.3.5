package egovframework.rte.psl.dataaccess.mybatis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import egovframework.rte.psl.dataaccess.TestBase;
import egovframework.rte.psl.dataaccess.dao.EmpMapper;
import egovframework.rte.psl.dataaccess.vo.EmpVO;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 *  == 개정이력(Modification Information) ==
 *   
 *   수정일      수정자           수정내용
 *  -------    --------    ---------------------------
 *   2014.01.22 권윤정  SimpleJdbcTestUtils -> JdbcTestUtils 변경
 *   2014.01.22 권윤정  SimpleJdbcTemplate -> JdbcTemplate 변경
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/context-*.xml" })
@TransactionConfiguration(transactionManager = "txManager", defaultRollback = false)
@Transactional
public class InLineParameterTest extends TestBase {

	@Resource(name = "empMapper")
	EmpMapper empMapper;

	@Before
	public void onSetUp() throws Exception {
		// 외부에 sql file 로부터 DB 초기화 (기존 테이블 삭제/생성 및
		// 초기데이터 구축)
		// Spring 의 JdbcTestUtils 사용,
		// continueOnError 플래그는 true로 설정 - cf.) DDL 이
		// 포함된 경우 rollback 에 유의
		JdbcTestUtils.executeSqlScript(new JdbcTemplate(dataSource), new ClassPathResource("META-INF/testdata/sample_schema_ddl_" + usingDBMS + ".sql"), true);

		// init data
		JdbcTestUtils.executeSqlScript(new JdbcTemplate(dataSource), new ClassPathResource("META-INF/testdata/sample_schema_initdata_" + usingDBMS + ".sql"), true);
	}

	public EmpVO makeVO() throws ParseException {
		EmpVO vo = new EmpVO();
		vo.setEmpNo(new BigDecimal(9000));
		vo.setEmpName("test Emp");
		vo.setJob("test Job");
		vo.setMgr(new BigDecimal(7839));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
		vo.setHireDate(sdf.parse("2009-02-09"));

		// mysql 은 소숫점 이하 자리가 .00 으로 기본 들어가게 되어 테스트 편의상
		// numeric(5) 로 선언하였음.
		if (isMysql || isHsql || isTibero) {
			vo.setSal(new BigDecimal("12345"));
			vo.setComm(new BigDecimal(100));
		} else {
			vo.setSal(new BigDecimal("12345.00"));
			vo.setComm(new BigDecimal(100.00));
		}
		// 10,'ACCOUNTING','NEW YORK'
		vo.setDeptNo(new BigDecimal(10));
		return vo;
	}

	public void checkResult(EmpVO vo, EmpVO resultVO) {
		assertNotNull(resultVO);
		assertEquals(vo.getEmpNo(), resultVO.getEmpNo());
		assertEquals(vo.getEmpName(), resultVO.getEmpName());
		assertEquals(vo.getJob(), resultVO.getJob());
		assertEquals(vo.getMgr(), resultVO.getMgr());
		assertEquals(vo.getHireDate(), resultVO.getHireDate());
		assertEquals(vo.getSal(), resultVO.getSal());
		assertEquals(vo.getComm(), resultVO.getComm());
		assertEquals(vo.getDeptNo(), resultVO.getDeptNo());
	}

	@Test
	public void testInLineParameterInsert() throws Exception {
		EmpVO vo = makeVO();

		// insert
		empMapper.insertEmp("insertEmptUsingInLineParam", vo);

		// select
		EmpVO resultVO = empMapper.selectEmp("selectEmp", vo);

		// check
		checkResult(vo, resultVO);
	}

	@Test
	public void testInLineParameterInsertWithNullValue() throws Exception {
		EmpVO vo = new EmpVO();
		// key 설정
		vo.setEmpNo(new BigDecimal(9000));

		// inline parameter 에서는 empty String 을 nullValue로 대체할 수 없음
		// ref.) http://www.nabble.com/inline-map-format%3A-empty-String-in-nullValue-td18905940.html
		vo.setJob(""); // cf.) oracle 인 경우 "" 는 null 과 같음!

		// insert
		empMapper.insertEmp("insertEmptUsingInLineParam", vo);

		// select
		EmpVO resultVO = empMapper.selectEmp("selectEmp", vo);

		// check
		assertNotNull(resultVO);
		assertEquals(vo.getEmpNo(), resultVO.getEmpNo());

		// inline parameter 에서는 empty String을
		// nullValue로 대체할 수 없음 확인!
		// cf.) parameterMap 케이스에서는

		if (isOracle || isTibero) {
			assertNull(resultVO.getJob()); // cf.) oracle 인 경우 "" 는 null 과 같음!
		} else {
			assertNotNull(resultVO.getJob());
		}
		assertNull(resultVO.getEmpName());
	}
}
