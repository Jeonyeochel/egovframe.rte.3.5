package egovframework.rte.fdl.excel.upload;

import java.math.BigDecimal;

import egovframework.rte.fdl.excel.EgovExcelMapping;
import egovframework.rte.fdl.excel.util.EgovExcelUtil;
import egovframework.rte.fdl.excel.vo.EmpVO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sjyoon
 *
 */
public class EgovExcelTestMapping extends EgovExcelMapping {

	private static final Logger LOGGER = LoggerFactory.getLogger(EgovExcelTestMapping.class);

	@Override
	public EmpVO mappingColumn(Row row) {
		Cell cell0 = row.getCell(0);
    	Cell cell1 = row.getCell(1);
    	Cell cell2 = row.getCell(2);

		EmpVO vo = new EmpVO();

		vo.setEmpNo(new BigDecimal(cell0.getNumericCellValue()));
		vo.setEmpName(EgovExcelUtil.getValue(cell1));
		vo.setJob(EgovExcelUtil.getValue(cell2));

		LOGGER.debug("########### vo is {}", vo.getEmpNo());
		LOGGER.debug("########### vo is {}", vo.getEmpName());
		LOGGER.debug("########### vo is {}", vo.getJob());

		return vo;
	}
}
