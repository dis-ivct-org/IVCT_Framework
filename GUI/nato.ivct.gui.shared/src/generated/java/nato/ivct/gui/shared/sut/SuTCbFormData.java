package nato.ivct.gui.shared.sut;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.form.AbstractFormData;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;
import org.eclipse.scout.rt.shared.data.form.properties.AbstractPropertyData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications
 * recommended.
 */
@Generated(value = "nato.ivct.gui.client.sut.SuTCbForm", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SuTCbFormData extends AbstractFormData {

	private static final long serialVersionUID = 1L;

	/**
	 * access method for property ActiveTsId.
	 */
	public String getActiveTsId() {
		return getActiveTsIdProperty().getValue();
	}

	/**
	 * access method for property ActiveTsId.
	 */
	public void setActiveTsId(String activeTsId) {
		getActiveTsIdProperty().setValue(activeTsId);
	}

	public ActiveTsIdProperty getActiveTsIdProperty() {
		return getPropertyByClass(ActiveTsIdProperty.class);
	}

	/**
	 * access method for property CbId.
	 */
	public String getCbId() {
		return getCbIdProperty().getValue();
	}

	/**
	 * access method for property CbId.
	 */
	public void setCbId(String cbId) {
		getCbIdProperty().setValue(cbId);
	}

	public CbIdProperty getCbIdProperty() {
		return getPropertyByClass(CbIdProperty.class);
	}

	/**
	 * access method for property SutId.
	 */
	public String getSutId() {
		return getSutIdProperty().getValue();
	}

	/**
	 * access method for property SutId.
	 */
	public void setSutId(String sutId) {
		getSutIdProperty().setValue(sutId);
	}

	public SutIdProperty getSutIdProperty() {
		return getPropertyByClass(SutIdProperty.class);
	}

	public SutTcExtraParameterTable getSutTcExtraParameterTable() {
		return getFieldByClass(SutTcExtraParameterTable.class);
	}

	public SutTcParameterTable getSutTcParameterTable() {
		return getFieldByClass(SutTcParameterTable.class);
	}

	public SutTcRequirementTable getSutTcRequirementTable() {
		return getFieldByClass(SutTcRequirementTable.class);
	}

	public static class ActiveTsIdProperty extends AbstractPropertyData<String> {

		private static final long serialVersionUID = 1L;
	}

	public static class CbIdProperty extends AbstractPropertyData<String> {

		private static final long serialVersionUID = 1L;
	}

	public static class SutIdProperty extends AbstractPropertyData<String> {

		private static final long serialVersionUID = 1L;
	}

	public static class SutTcExtraParameterTable extends AbstractTableFieldBeanData {

		private static final long serialVersionUID = 1L;

		@Override
		public SutTcExtraParameterTableRowData addRow() {
			return (SutTcExtraParameterTableRowData) super.addRow();
		}

		@Override
		public SutTcExtraParameterTableRowData addRow(int rowState) {
			return (SutTcExtraParameterTableRowData) super.addRow(rowState);
		}

		@Override
		public SutTcExtraParameterTableRowData createRow() {
			return new SutTcExtraParameterTableRowData();
		}

		@Override
		public Class<? extends AbstractTableRowData> getRowType() {
			return SutTcExtraParameterTableRowData.class;
		}

		@Override
		public SutTcExtraParameterTableRowData[] getRows() {
			return (SutTcExtraParameterTableRowData[]) super.getRows();
		}

		@Override
		public SutTcExtraParameterTableRowData rowAt(int index) {
			return (SutTcExtraParameterTableRowData) super.rowAt(index);
		}

		public void setRows(SutTcExtraParameterTableRowData[] rows) {
			super.setRows(rows);
		}

		public static class SutTcExtraParameterTableRowData extends AbstractTableRowData {

			private static final long serialVersionUID = 1L;
			public static final String fileName = "fileName";
			private String m_fileName;

			public String getFileName() {
				return m_fileName;
			}

			public void setFileName(String newFileName) {
				m_fileName = newFileName;
			}
		}
	}

	public static class SutTcParameterTable extends AbstractTableFieldBeanData {

		private static final long serialVersionUID = 1L;

		@Override
		public SutTcParameterTableRowData addRow() {
			return (SutTcParameterTableRowData) super.addRow();
		}

		@Override
		public SutTcParameterTableRowData addRow(int rowState) {
			return (SutTcParameterTableRowData) super.addRow(rowState);
		}

		@Override
		public SutTcParameterTableRowData createRow() {
			return new SutTcParameterTableRowData();
		}

		@Override
		public Class<? extends AbstractTableRowData> getRowType() {
			return SutTcParameterTableRowData.class;
		}

		@Override
		public SutTcParameterTableRowData[] getRows() {
			return (SutTcParameterTableRowData[]) super.getRows();
		}

		@Override
		public SutTcParameterTableRowData rowAt(int index) {
			return (SutTcParameterTableRowData) super.rowAt(index);
		}

		public void setRows(SutTcParameterTableRowData[] rows) {
			super.setRows(rows);
		}

		public static class SutTcParameterTableRowData extends AbstractTableRowData {

			private static final long serialVersionUID = 1L;
			public static final String id = "id";
			public static final String parentId = "parentId";
			public static final String parameterName = "parameterName";
			public static final String parameterValue = "parameterValue";
			private Long m_id;
			private Long m_parentId;
			private String m_parameterName;
			private String m_parameterValue;

			public Long getId() {
				return m_id;
			}

			public void setId(Long newId) {
				m_id = newId;
			}

			public Long getParentId() {
				return m_parentId;
			}

			public void setParentId(Long newParentId) {
				m_parentId = newParentId;
			}

			public String getParameterName() {
				return m_parameterName;
			}

			public void setParameterName(String newParameterName) {
				m_parameterName = newParameterName;
			}

			public String getParameterValue() {
				return m_parameterValue;
			}

			public void setParameterValue(String newParameterValue) {
				m_parameterValue = newParameterValue;
			}
		}
	}

	public static class SutTcRequirementTable extends AbstractTableFieldBeanData {

		private static final long serialVersionUID = 1L;

		@Override
		public SutTcRequirementTableRowData addRow() {
			return (SutTcRequirementTableRowData) super.addRow();
		}

		@Override
		public SutTcRequirementTableRowData addRow(int rowState) {
			return (SutTcRequirementTableRowData) super.addRow(rowState);
		}

		@Override
		public SutTcRequirementTableRowData createRow() {
			return new SutTcRequirementTableRowData();
		}

		@Override
		public Class<? extends AbstractTableRowData> getRowType() {
			return SutTcRequirementTableRowData.class;
		}

		@Override
		public SutTcRequirementTableRowData[] getRows() {
			return (SutTcRequirementTableRowData[]) super.getRows();
		}

		@Override
		public SutTcRequirementTableRowData rowAt(int index) {
			return (SutTcRequirementTableRowData) super.rowAt(index);
		}

		public void setRows(SutTcRequirementTableRowData[] rows) {
			super.setRows(rows);
		}

		public static class SutTcRequirementTableRowData extends AbstractTableRowData {

			private static final long serialVersionUID = 1L;
			public static final String requirementId = "requirementId";
			public static final String requirementDesc = "requirementDesc";
			private String m_requirementId;
			private String m_requirementDesc;

			public String getRequirementId() {
				return m_requirementId;
			}

			public void setRequirementId(String newRequirementId) {
				m_requirementId = newRequirementId;
			}

			public String getRequirementDesc() {
				return m_requirementDesc;
			}

			public void setRequirementDesc(String newRequirementDesc) {
				m_requirementDesc = newRequirementDesc;
			}
		}
	}
}
