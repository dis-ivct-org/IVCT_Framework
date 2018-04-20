package nato.ivct.gui.shared.sut;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications
 * recommended.
 */
@Generated(value = "nato.ivct.gui.client.sut.SuTBadgeTablePage", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SuTBadgeTablePageData extends AbstractTablePageData {

	private static final long serialVersionUID = 1L;

	@Override
	public SuTBadgeTableRowData addRow() {
		return (SuTBadgeTableRowData) super.addRow();
	}

	@Override
	public SuTBadgeTableRowData addRow(int rowState) {
		return (SuTBadgeTableRowData) super.addRow(rowState);
	}

	@Override
	public SuTBadgeTableRowData createRow() {
		return new SuTBadgeTableRowData();
	}

	@Override
	public Class<? extends AbstractTableRowData> getRowType() {
		return SuTBadgeTableRowData.class;
	}

	@Override
	public SuTBadgeTableRowData[] getRows() {
		return (SuTBadgeTableRowData[]) super.getRows();
	}

	@Override
	public SuTBadgeTableRowData rowAt(int index) {
		return (SuTBadgeTableRowData) super.rowAt(index);
	}

	public void setRows(SuTBadgeTableRowData[] rows) {
		super.setRows(rows);
	}

	public static class SuTBadgeTableRowData extends AbstractTableRowData {

		private static final long serialVersionUID = 1L;
		public static final String badgeId = "badgeId";
		public static final String badgeDesc = "badgeDesc";
		public static final String suTBadgeResult = "suTBadgeResult";
		private String m_badgeId;
		private String m_badgeDesc;
		private String m_suTBadgeResult;

		public String getBadgeId() {
			return m_badgeId;
		}

		public void setBadgeId(String newBadgeId) {
			m_badgeId = newBadgeId;
		}

		public String getBadgeDesc() {
			return m_badgeDesc;
		}

		public void setBadgeDesc(String newBadgeDesc) {
			m_badgeDesc = newBadgeDesc;
		}

		public String getSuTBadgeResult() {
			return m_suTBadgeResult;
		}

		public void setSuTBadgeResult(String newSuTBadgeResult) {
			m_suTBadgeResult = newSuTBadgeResult;
		}
	}
}
