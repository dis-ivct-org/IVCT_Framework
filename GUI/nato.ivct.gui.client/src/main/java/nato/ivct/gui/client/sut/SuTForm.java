package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.DetailsHorizontalSplitterBox.CapabilityStatusBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.DescrField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.NameField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.SutVendorField;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTFormData;


@FormData(value = SuTFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTForm extends AbstractForm {

    private String sutId = null;
    private String title = null;


    public SuTForm(String formTitle) {
        title = formTitle;
    }


    @Override
    protected String getConfiguredTitle() {
        if (title != null)
            return title;
        else
            return TEXTS.get("SuT");
    }


    public void startView() {
        startInternal/* Exclusive */(new ViewHandler());
    }


    public MainBox getMainBox() {
        return getFieldByClass(MainBox.class);
    }


    public GeneralBox getGeneralBox() {
        return getFieldByClass(GeneralBox.class);
    }


    public DescrField getDescrField() {
        return getFieldByClass(DescrField.class);
    }


    public SutVendorField getSutVendorField() {
        return getFieldByClass(SutVendorField.class);
    }


    public CapabilityStatusBox getDetailsBox() {
        return getFieldByClass(CapabilityStatusBox.class);
    }


    public NameField getNameField() {
        return getFieldByClass(NameField.class);
    }


    @FormData
    public String getSutId() {
        return sutId;
    }


    @FormData
    public void setSutId(final String _sutId) {
        sutId = _sutId;
    }


    @Override
    public Object computeExclusiveKey() {
        return getSutId();
    }


    @Override
    protected int getConfiguredDisplayHint() {
        // TODO Auto-generated method stub
        return IForm.DISPLAY_HINT_VIEW;
    }

    @Order(1000)
    public class MainBox extends AbstractGroupBox {

        @Order(1000)
        public class MainBoxHorizontalSplitBox extends AbstractSplitBox {
            @Override
            protected boolean getConfiguredSplitHorizontal() {
                // split horizontal
                return false;
            }


            @Override
            protected double getConfiguredSplitterPosition() {
                return 0.35;
            }

            @Order(1000)
            public class GeneralBox extends AbstractGroupBox {
                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("GeneralInformation");
                }


                // set all fields of this box to read-only
                @Override
                public boolean isEnabled() {
                    return false;
                }

                @Order(1000)
                public class NameField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Name");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 128;
                    }
                }

                @Order(1500)
                public class VersionField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Version");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 64;
                    }
                }

                @Order(2000)
                public class SutVendorField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Vendor");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 128;
                    }
                }

                @Order(3000)
                public class DescrField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Description");
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 3;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 2;
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 256;
                    }


                    // set to multi-line
                    @Override
                    protected boolean getConfiguredMultilineText() {
                        return true;
                    }
                }

                @Order(4000)
                public class FederationSettingsBox extends AbstractGroupBox {
                    @Override
                    protected int getConfiguredGridColumnCount() {
                        return 2;
                    }


                    // don't show border
                    @Override
                    protected boolean getConfiguredBorderVisible() {
                        return false;
                    }

                    @Order(4100)
                    public class RtiSettingDesignator extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("RTIConnection");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }

                    @Order(4200)
                    public class FederationName extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("FederationName");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }

                    @Order(4300)
                    public class FederateName extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("FederateName");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }
                }
            }

            @Order(2000)
            public class DetailsHorizontalSplitterBox extends AbstractSplitBox {

                @Override
                protected boolean getConfiguredSplitHorizontal() {
                    // split horizontal
                    return false;
                }


                @Override
                protected double getConfiguredSplitterPosition() {
                    return 0.35;
                }

                @Order(1000)
                public class CapabilityStatusBox extends AbstractGroupBox {
                    @Override
                    protected boolean getConfiguredVisible() {
                        // !!! TODO Hide this box until it has no real content !!!
                        return true;
                    }

                    @Order(1000)
                    public class SutCapabilityStatusTableField extends AbstractTableField<SutCapabilityStatusTableField.SutCapabilityStatusTable> {
                        @Override
                        protected int getConfiguredGridW() {
                            return 3;
                        }


                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("CapabilityStatus");
                        }

                        public class SutCapabilityStatusTable extends AbstractTable {
                            @Order(1000)
                            public class CbBadgeIDColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("BadgeId");
                                }


                                @Override
                                protected boolean getConfiguredVisible() {
                                    return true;
                                }
                            }

                            @Order(2000)
                            public class CbBadgeNameColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("BadgeName");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 300;
                                }
                            }

                            @Order(3000)
                            public class CbBadgeStatusColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("BadgeConformanceStatus");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 100;
                                }
                            }
                        }
                    }
                }

                @Order(2000)
                public class TestReportBox extends AbstractGroupBox {

                    @Order(2000)
                    public class TestReportTableField extends AbstractTableField<TestReportTableField.TestReportTable> {

                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("TestReports");
                        }

                        //						@Override
                        //						protected int getConfiguredGridW() {
                        //							return 3;
                        //						}

                        @Order(1000)
                        public class TestReportTable extends AbstractTable {

                            @Override
                            protected boolean getConfiguredMultiSelect() {
                                // only a single row can be selected
                                return false;
                            }

                            @Order(1000)
                            public class FileNameColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("FileName");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 400;
                                }
                            }


                            public FileNameColumn getFileNameColumn() {
                                return getColumnSet().getColumnByClass(FileNameColumn.class);
                            }


                            // called on double-click on a row
                            @Override
                            protected void execRowAction(ITableRow row) {
                                final TestReportForm form = new TestReportForm();
                                // set SUT Id and requested report file name
                                form.setSutId(getSutId());
                                form.setReportFileName(getTable().getFileNameColumn().getValue(getSelectedRow()));
                                //load and open the form
                                form.startView();
                            }
                        }
                    }
                }
            }
        }

    }

    public class ViewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            final ISuTService service = BEANS.get(ISuTService.class);
            SuTFormData formData = new SuTFormData();
            exportFormData(formData);
            formData = service.load(formData);
            importFormData(formData);

            //			setEnabledPermission(new UpdateSuTPermission());
        }
    }
}
