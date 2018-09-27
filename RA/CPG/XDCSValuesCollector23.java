package com.rockwell.om.execution.manual;

import com.datasweep.compatibility.client.ActivityResult;
import com.datasweep.compatibility.client.DCInstance;
import com.datasweep.compatibility.client.DataCollectionItem;
import com.datasweep.compatibility.client.DataCollectionSet;
import com.datasweep.compatibility.client.DsList;
import com.datasweep.compatibility.client.Error;
import com.datasweep.compatibility.client.Keyed;
import com.datasweep.compatibility.client.LDTagSetDefinition;
import com.datasweep.compatibility.client.LDTemplate;
import com.datasweep.compatibility.client.ProcessOrderItem;
import com.datasweep.compatibility.client.ProcessStep;
import com.datasweep.compatibility.client.ProcessStepControlRecipe;
import com.datasweep.compatibility.client.ProductionLine;
import com.datasweep.compatibility.client.Response;
import com.datasweep.compatibility.client.RuntimeDCS;
import com.datasweep.compatibility.client.Step;
import com.datasweep.compatibility.client.Subroutine;
import com.datasweep.compatibility.client.WorkCenter;
import com.datasweep.compatibility.manager.ServerImpl;
import com.datasweep.compatibility.manager.SubroutineManager;
import com.datasweep.compatibility.pnuts.Functions;
import com.datasweep.compatibility.ui.ActivityControl;
import com.datasweep.compatibility.ui.Color;
import com.datasweep.compatibility.ui.FlatButton;
import com.datasweep.compatibility.ui.FlatLabel;
import com.datasweep.compatibility.ui.ObjectBinder;
import com.datasweep.compatibility.ui.Panel;
import com.datasweep.compatibility.ui.ScrollPane;
import com.datasweep.core.eventlog.EventLog;
import com.datasweep.plantops.property.editor.EnumPropertyEditor;
import com.datasweep.plantops.swing.ImageHolder;
import com.rockwell.activity.CComponentEvent;
import com.rockwell.activity.CComponentEventListener;
import com.rockwell.activity.ItemDescriptor;
import com.rockwell.af.activity.controls.DataCollectorConfig;
import com.rockwell.af.activity.controls.DataCollectorConfig.DCConfigItem;
import com.rockwell.af.activity.ui.UIUtilities;
import com.rockwell.af.app.constants.IConfigurationConstants;
import com.rockwell.af.foundation.object.AbstractSupport;
import com.rockwell.af.foundation.object.BasicPanelCom;
import com.rockwell.af.foundation.object.FoundationObjectSupport;
import com.rockwell.af.foundation.object.LDAdaptor;
import com.rockwell.af.utility.CPGVersionInfoHelper;
import com.rockwell.om.app.utility.RecipeResource;
import com.rockwell.om.app.utility.RecipeResourceSet;
import com.rockwell.om.common.utility.UserTransactionHelper;
import com.rockwell.om.constants.IRuntimeActivityConstants;
import com.rockwell.om.control.ReadOnlyStringPropertyEditor;
import com.rockwell.smartedit.SmartEdit;
import com.rockwell.transactiongrouping.UserTransaction;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;

public final class DCSValuesCollector23 extends ActivityControl
  implements ICPG_Constants, ICPG_ExecutionConstants, ICPG_ManualMessages, IConfigurationConstants, IRuntimeActivityConstants
{
  public static final String PROCESS_STEP = "processStep";
  public ImageHolder FINISH_BUTTON_IMAGE_HOLDER = UIUtilities.getImage(CPG_ManualProduction23.class, "images/btnFinish.gif");
  public static final String COLLECT_VALUES_BT_TEXT = "Collect";
  public transient ImageHolder BACK_BUTTON_IMAGE_HOLDER = UIUtilities.getImage(CPG_ManualProduction23.class, "images/btnBack.gif");
  public static final String COLLECT_VALUES_BT_TT = "COLLECT_VALUES_BT_TT_TEXT";
  public static final String COLLECT_VALUES_BT_TT_TEXT = "Collect Values";
  public static final String CANCEL_COLLECT_VALUES_BT_TEXT = "Cancel";
  public static final String CANCEL_COLLECT_VALUES_BT_TT = "CANCEL_COLLECT_VALUES_BT_TT_TEXT";
  public static final String CANCEL_COLLECT_VALUES_BT_TT_TEXT = "Cancel Collecting Values";
  public static final String DCS_VALUES_COLLECTOR_TITLE_TT = "DCS_VALUES_COLLECTOR_TITLE";
  private Panel pnlToolbar = null;
  private FlatButton collectBt = null;
  private FlatButton cancelBt = null;
  private int MASTER_W = 800;
  private Panel collector = null;
  private ScrollPane scrollPane1;
  private FlatLabel orderLb;
  public static final String FIELD_1_LABEL_TEXT = "Field 1";
  private FlatLabel orderQtyLb;
  public static final String FIELD_3_LABEL_TEXT = "Field 3";
  private FlatLabel materialLb;
  public static final String FIELD_2_LABEL_TEXT = "Field 2";
  public static final String BLANK = "[]";
  private int orderPnRows = 1;
  private int orderPnColumns = 3;
  private Panel orderPn;
  private ObjectBinder poiObjectBinder;
  private ArrayList<Panel> panelList = null;
  private ArrayList<BasicPanelCom> list = new ArrayList();
  private HashMap<String, CPG_ScalesActivity23> scaleControlHolder;
  private String dcsName;
  private ItemDescriptor[] configurations = null;
  private ItemDescriptor[] outputDescriptors = null;
  private ItemDescriptor[] inputDescriptors = null;
  private transient ProcessOrderItem poi;
  private transient ProcessStep processStep;
  private transient Keyed foundationObject;
  private StringBuffer inputErrorBuffer = new StringBuffer();
  public String CONFIGURATION_ITEM_CONFIGURE_OWNER = "ConfiguratonOwner";
  public static final short CONFIG_OWNER_ROUTE_STEP = 0;
  public static final short CONFIG_OWNER_FOUNDATIONOBJECT = 1;
  private transient Keyed owner = null;
  private String liveData_url;
  private String liveData_path;
  private String liveData_plc;
  private LDAdaptor lDAdaptor;
  private transient DataCollectorConfig config = null;
  private transient RuntimeDCS dcs;
  private transient DCInstance ins;
  private SmartEdit orderValueLb1;
  private SmartEdit materialValueLb1;
  private SmartEdit orderQtyValueLb1;
  private boolean isCollectClick = false;
  private boolean isCancelClick = false;

  public DCSValuesCollector23()
  {
    setDefaultConfigValue();
    setMessages(UIUtilities.getMessagesHolder(this, "CPG_Messages"));
    setToolTipId(UIUtilities.getMessageIdHolder(this, "DCS_VALUES_COLLECTOR_TITLE"));
  }

  public boolean activityStart()
  {
    buildView();
    return super.activityStart();
  }

  private Keyed getRecipe(ProcessStep paramProcessStep)
  {
    if (paramProcessStep.getProcessStepControlRecipe().getMasterRecipe() != null)
      return paramProcessStep.getProcessStepControlRecipe().getMasterRecipe();
    return paramProcessStep.getProcessStepControlRecipe().getTemplateRecipe();
  }

  private Keyed getOrder(ProcessStep paramProcessStep)
  {
    return paramProcessStep.getProcessStepControlRecipe().getProcessOrderItem().getParent();
  }

  private void buildBind(Panel paramPanel)
  {
    this.orderValueLb1 = new SmartEdit();
    this.orderValueLb1.setName("orderValueLb");
    this.orderValueLb1.setActivityName("orderValueLb");
    this.orderValueLb1.setLocation(0, 0);
    this.orderValueLb1.setPreferredSize(BOUND_SMART_EDIT_DIM_300);
    this.orderValueLb1.setAnchor(5);
    this.orderValueLb1.setDock(0);
    this.orderValueLb1.setTabStop(true);
    this.orderValueLb1.setVisible(true);
    this.orderValueLb1.setBackColor(STANDARD_BACKGROUND);
    this.orderValueLb1.setLabelTextAlign(20);
    this.orderValueLb1.setLabelWidth(100);
    this.orderValueLb1.setLabelBackColor(STANDARD_BACKGROUND);
    this.orderValueLb1.setLabelFont(FONT_PLAIN_12);
    this.orderValueLb1.setLabelText("Order");
    this.orderValueLb1.setFont(FONT_BOLD_12);
    this.orderValueLb1.setEditorBorderStyle(0);
    this.orderValueLb1.setMessages(UIUtilities.getMessagesHolder(this.orderValueLb1, "CPG_Messages"));
    this.orderValueLb1.setLabelTextId(UIUtilities.getMessageIdHolder(this.orderValueLb1, "MANUAL_PRODUCE_ORDER_LABEL"));
    this.orderValueLb1.setEditorSupportClass(ReadOnlyStringPropertyEditor.class.getName());
    this.materialValueLb1 = new SmartEdit();
    this.materialValueLb1.setName("materialValueLb");
    this.materialValueLb1.setActivityName("materialValueLb");
    this.materialValueLb1.setLocation(0, 0);
    this.materialValueLb1.setPreferredSize(BOUND_SMART_EDIT_DIM_300);
    this.materialValueLb1.setAnchor(5);
    this.materialValueLb1.setDock(0);
    this.materialValueLb1.setTabStop(true);
    this.materialValueLb1.setVisible(true);
    this.materialValueLb1.setBackColor(STANDARD_BACKGROUND);
    this.materialValueLb1.setLabelTextAlign(20);
    this.materialValueLb1.setLabelWidth(100);
    this.materialValueLb1.setLabelBackColor(STANDARD_BACKGROUND);
    this.materialValueLb1.setLabelFont(FONT_PLAIN_12);
    this.materialValueLb1.setLabelText("Material");
    this.materialValueLb1.setEditorBorderStyle(0);
    this.materialValueLb1.setFont(FONT_BOLD_12);
    this.materialValueLb1.setMessages(UIUtilities.getMessagesHolder(this.materialValueLb1, "CPG_Messages"));
    this.materialValueLb1.setLabelTextId(UIUtilities.getMessageIdHolder(this.materialValueLb1, "MANUAL_PRODUCE_MATERIAL_LABEL"));
    this.materialValueLb1.setEditorSupportClass(ReadOnlyStringPropertyEditor.class.getName());
    this.orderQtyValueLb1 = new SmartEdit();
    this.orderQtyValueLb1.setName("orderQtyValueLb1");
    this.orderQtyValueLb1.setActivityName("orderQtyValueLb");
    this.orderQtyValueLb1.setLocation(0, 0);
    this.orderQtyValueLb1.setPreferredSize(BOUND_SMART_EDIT_DIM_300);
    this.orderQtyValueLb1.setAnchor(5);
    this.orderQtyValueLb1.setDock(0);
    this.orderQtyValueLb1.setTabStop(true);
    this.orderQtyValueLb1.setVisible(true);
    this.orderQtyValueLb1.setBackColor(STANDARD_BACKGROUND);
    this.orderQtyValueLb1.setLabelTextAlign(20);
    this.orderQtyValueLb1.setLabelWidth(100);
    this.orderQtyValueLb1.setLabelBackColor(STANDARD_BACKGROUND);
    this.orderQtyValueLb1.setLabelFont(FONT_PLAIN_12);
    this.orderQtyValueLb1.setLabelText("Order Quantity");
    this.orderQtyValueLb1.setEditorBorderStyle(0);
    this.orderQtyValueLb1.setFont(FONT_BOLD_12);
    this.orderQtyValueLb1.setMessages(UIUtilities.getMessagesHolder(this.orderQtyValueLb1, "CPG_Messages"));
    this.orderQtyValueLb1.setLabelTextId(UIUtilities.getMessageIdHolder(this.orderQtyValueLb1, "MANUAL_PRODUCE_ORDER_QUANTITY_LABEL"));
    this.orderQtyValueLb1.setEditorSupportClass(ReadOnlyStringPropertyEditor.class.getName());
    paramPanel.add(this.orderValueLb1);
    paramPanel.add(this.materialValueLb1);
    paramPanel.add(this.orderQtyValueLb1);
  }

  private void buildView()
  {
    if (this.processStep != null)
    {
      localObject = null;
      try
      {
        localObject = RecipeResourceSet.getRecipeResourceSet(this.processStep.getProcessStepControlRecipe());
      }
      catch (Exception localException1)
      {
        localException1.printStackTrace();
      }
      if (localObject != null)
      {
        Vector localVector = ((RecipeResourceSet)localObject).getAvailableRecipeResources(this.processStep);
        for (int i = 0; i < localVector.size(); i++)
        {
          if (((RecipeResource)localVector.get(i)).getProductionLine() != null)
            this.lDAdaptor.setProductionLineName(((RecipeResource)localVector.get(i)).getProductionLine().getName());
          if (((RecipeResource)localVector.get(i)).getWorkCenter() != null)
            this.lDAdaptor.setWorkCenterName(((RecipeResource)localVector.get(i)).getWorkCenter().getName());
        }
      }
      this.processStep = ((ProcessStep)getInputItem("processStep"));
      this.foundationObject = ((Keyed)getInputItem("foundationObject"));
      this.liveData_url = ((String)getConfigurationItem("LiveDataWebServiceURL"));
      this.liveData_path = ((String)getConfigurationItem("LiveDataWebServiceServerPath"));
      this.liveData_plc = ((String)getConfigurationItem("PLC"));
      this.lDAdaptor = new LDAdaptor();
      if (this.liveData_plc == null)
        try
        {
          this.lDAdaptor.setPlc(FoundationObjectSupport.getFoundationObjectByKeyedObject(this.foundationObject).getPLC());
        }
        catch (Exception localException2)
        {
          localException2.printStackTrace();
        }
      this.lDAdaptor.setGroupNamePrefix("DC_");
      this.lDAdaptor.setLDServicePath(this.liveData_path);
      this.lDAdaptor.setlDServiceURL(this.liveData_url);
      if (this.foundationObject != null)
        this.lDAdaptor.setFoName(this.foundationObject.getName());
    }
    setPreferredSize(new Dimension(810, 600));
    setDock(5);
    setLayoutStyle(3);
    this.collectBt = new FlatButton();
    this.collectBt.setName("collectBt");
    this.collectBt.setActivityName("collectBt");
    this.collectBt.setFont(FONT_PLAIN_12);
    this.collectBt.setAutoResize(false);
    this.collectBt.setIconTextGap(1);
    this.collectBt.setPreferredSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.collectBt.setMinimumSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.collectBt.setMaximumSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.collectBt.setTextAlign(66);
    this.collectBt.setAnchor(0);
    this.collectBt.setEnabled(true);
    this.collectBt.setRolloverColor(IRuntimeActivityConstants.BUTTON_ROLLOVER_COLOR);
    this.collectBt.setForeColor(STANDARD_FOREGROUND);
    this.collectBt.setBackColor(STANDARD_BACKGROUND);
    this.collectBt.setText("Collect");
    this.collectBt.setMessages(UIUtilities.getMessagesHolder(this.collectBt, "CPG_Messages"));
    this.collectBt.setToolTipId(UIUtilities.getMessageIdHolder(this.collectBt, "COLLECT_VALUES_BT_TT_TEXT"));
    this.collectBt.setDock(0);
    this.collectBt.setImageAlign(34);
    this.collectBt.setImageHolder(this.FINISH_BUTTON_IMAGE_HOLDER);
    this.collectBt.setStyle(3);
    this.collectBt.setRoundBorderSize(15);
    Object localObject = new CComponentEventListener()
    {
      public Object ccomponentEventFired(CComponentEvent paramAnonymousCComponentEvent)
      {
        if ("click".equals(paramAnonymousCComponentEvent.getEvent()))
          DCSValuesCollector23.this.onCollectBtClicked();
        return null;
      }
    };
    this.collectBt.addCComponentEventListener((CComponentEventListener)localObject);
    this.cancelBt = new FlatButton();
    this.cancelBt.setName("cancelBt");
    this.cancelBt.setActivityName("cancelBt");
    this.cancelBt.setAutoResize(false);
    this.cancelBt.setIconTextGap(1);
    this.cancelBt.setPreferredSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.cancelBt.setMinimumSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.cancelBt.setMaximumSize(IRuntimeActivityConstants.TOOLBAR_BUTTON_SIZE);
    this.cancelBt.setTextAlign(66);
    this.cancelBt.setAnchor(0);
    this.cancelBt.setEnabled(true);
    this.cancelBt.setRolloverColor(IRuntimeActivityConstants.BUTTON_ROLLOVER_COLOR);
    this.cancelBt.setForeColor(STANDARD_FOREGROUND);
    this.cancelBt.setBackColor(STANDARD_BACKGROUND);
    this.cancelBt.setFont(FONT_PLAIN_12);
    this.cancelBt.setText("Cancel");
    this.cancelBt.setMessages(UIUtilities.getMessagesHolder(this.cancelBt, "CPG_Messages"));
    this.cancelBt.setToolTipId(UIUtilities.getMessageIdHolder(this.cancelBt, "CANCEL_COLLECT_VALUES_BT_TT_TEXT"));
    this.cancelBt.setDock(0);
    this.cancelBt.setImageAlign(34);
    this.cancelBt.setImageHolder(this.BACK_BUTTON_IMAGE_HOLDER);
    this.cancelBt.setStyle(3);
    this.cancelBt.setRoundBorderSize(15);
    CComponentEventListener local2 = new CComponentEventListener()
    {
      public Object ccomponentEventFired(CComponentEvent paramAnonymousCComponentEvent)
      {
        if ("click".equals(paramAnonymousCComponentEvent.getEvent()))
          DCSValuesCollector23.this.onCancelBtClicked();
        return null;
      }
    };
    this.cancelBt.addCComponentEventListener(local2);
    Panel localPanel1 = new Panel();
    localPanel1.setName("topBtPn");
    localPanel1.setActivityName("topBtPn");
    localPanel1.setLocation(new Point(0, 0));
    localPanel1.setHgap(10);
    localPanel1.setBorderStyle(0);
    localPanel1.setPreferredSize(new Dimension(750, 50));
    localPanel1.setVgap(1);
    localPanel1.setTabStop(false);
    localPanel1.setTabIndex(0);
    localPanel1.setLayoutStyle(3);
    localPanel1.setFlowAlignment(0);
    localPanel1.setAnchor(0);
    localPanel1.setEnabled(true);
    localPanel1.setDock(0);
    localPanel1.setToolTipText(null);
    localPanel1.setForeColor(STANDARD_FOREGROUND);
    localPanel1.setBackColor(STANDARD_BACKGROUND);
    localPanel1.add(this.collectBt);
    localPanel1.add(this.cancelBt);
    this.pnlToolbar = new Panel();
    this.pnlToolbar.setName("pnlToolbar");
    this.pnlToolbar.setActivityName("pnlToolbar");
    this.pnlToolbar.setLocation(new Point(0, 30));
    this.pnlToolbar.setHgap(0);
    this.pnlToolbar.setBorderStyle(0);
    this.pnlToolbar.setPreferredSize(new Dimension(this.MASTER_W, 100));
    this.pnlToolbar.setVgap(0);
    this.pnlToolbar.setTabStop(true);
    this.pnlToolbar.setTabIndex(0);
    this.pnlToolbar.setLayoutStyle(3);
    this.pnlToolbar.setAnchor(0);
    this.pnlToolbar.setGridLayoutColumns(2);
    this.pnlToolbar.setGridLayoutRows(1);
    this.pnlToolbar.setEnabled(true);
    this.pnlToolbar.setDock(1);
    this.pnlToolbar.setToolTipText(null);
    this.pnlToolbar.setBackColor(STANDARD_BACKGROUND);
    this.pnlToolbar.setForeColor(STANDARD_FOREGROUND);
    this.pnlToolbar.add(localPanel1);
    this.orderPn = new Panel();
    this.orderPn.setName("orderPn");
    this.orderPn.setActivityName("orderPn");
    this.orderPn.setHgap(0);
    this.orderPn.setVgap(20);
    this.orderPn.setBorderStyle(0);
    this.orderPn.setPreferredSize(new Dimension(750, 50));
    this.orderPn.setLocation(0, 50);
    this.orderPn.setLayoutStyle(1);
    this.orderPn.setAnchor(0);
    this.orderPn.setGridLayoutColumns(this.orderPnColumns);
    this.orderPn.setGridLayoutRows(this.orderPnRows);
    this.orderPn.setEnabled(true);
    this.orderPn.setForeColor(STANDARD_FOREGROUND);
    this.orderPn.setBackColor(STANDARD_BACKGROUND);
    this.orderPn.setDock(1);
    this.orderPn.setToolTipText(null);
    this.orderPn.setTabIndex(0);
    this.orderPn.setTabStop(false);
    this.orderPn.setGridLayoutColumns(this.orderPnColumns);
    this.orderPn.setGridLayoutRows(this.orderPnRows);
    buildBind(this.orderPn);
    this.poiObjectBinder = new ObjectBinder();
    this.poiObjectBinder.setName("poiObjectBinder");
    this.poiObjectBinder.setActivityName("poiObjectBinder");
    this.poiObjectBinder.setBoundClassType("com.datasweep.compatibility.client.ProcessOrderItem");
    this.poiObjectBinder.addControl(this.orderValueLb1, "orderName", null);
    this.poiObjectBinder.addControl(this.orderQtyValueLb1, "quantity", null);
    this.poiObjectBinder.addControl(this.materialValueLb1, "partNumber", null);
    this.pnlToolbar.add(this.orderPn);
    this.pnlToolbar.add(this.poiObjectBinder);
    add(this.pnlToolbar);
    add(this.poiObjectBinder);
    if (this.processStep == null)
      return;
    ProcessStepControlRecipe localProcessStepControlRecipe = this.processStep.getProcessStepControlRecipe();
    this.poi = localProcessStepControlRecipe.getProcessOrderItem();
    if (this.poi.getClass().getName().equalsIgnoreCase(this.poiObjectBinder.getBoundClassType()))
      this.poiObjectBinder.setBoundObject(this.poi);
    this.scrollPane1 = new ScrollPane();
    this.scrollPane1.setLocation(new Point(0, 100));
    this.scrollPane1.setPreferredSize(new Dimension(800, 500));
    this.scrollPane1.setTabIndex(1);
    this.scrollPane1.setAnchor(5);
    this.scrollPane1.setOpaque(true);
    this.scrollPane1.setEnabled(true);
    this.scrollPane1.setHorizontalScrollBarPolicy(30);
    this.scrollPane1.setWheelScrollingEnabled(true);
    this.scrollPane1.setForeColor(new Color(0, 0, 64));
    this.scrollPane1.setFont(new Font("Dialog", 0, 12));
    this.scrollPane1.setToolTipText(null);
    this.scrollPane1.setDock(5);
    this.scrollPane1.setTabStop(true);
    this.scrollPane1.setVerticalScrollBarPolicy(20);
    this.scrollPane1.setMessages(UIUtilities.getMessagesHolder(this.scrollPane1, ""));
    this.scrollPane1.setName("scrollPane1");
    this.scrollPane1.setActivityName("scrollPane1");
    this.collector = new Panel();
    this.collector.setRoundBorderColor(new Color(0, 0, 0));
    this.collector.setHgap(0);
    this.collector.setBorderStyle(0);
    this.collector.setRoundBorderSize(0);
    this.collector.setVgap(0);
    this.collector.setTabIndex(1);
    this.collector.setBackColor(new Color(255, 255, 255));
    this.collector.setGridLayoutColumns(2);
    this.collector.setAnchor(0);
    this.collector.setOpaque(true);
    this.collector.setGridLayoutRows(2);
    this.collector.setEnabled(true);
    this.collector.setForeColor(new Color(0, 0, 64));
    this.collector.setFont(new Font("Dialog", 0, 12));
    this.collector.setFlowAlignment(32);
    this.collector.setToolTipText(null);
    this.collector.setDock(5);
    this.collector.setTabStop(true);
    this.collector.setName("panel1");
    this.collector.setActivityName("panel1");
    this.collector.setLayoutStyle(3);
    if (this.processStep == null)
      return;
    if (this.list == null)
      this.list = new ArrayList();
    this.owner = null;
    int j = 0;
    if (getConfigurationItem(this.CONFIGURATION_ITEM_CONFIGURE_OWNER) != null)
      j = ((Short)getConfigurationItem(this.CONFIGURATION_ITEM_CONFIGURE_OWNER)).shortValue();
    if (j == 0)
      this.owner = this.processStep.getRouteStep();
    else
      this.owner = this.foundationObject;
    this.list = getConfigComs(this.owner);
    if ((this.list == null) || (this.list.size() == 0))
      return;
    int k = 0;
    Dimension localDimension = BasicPanelCom.caculateMaxDimention(null, BasicPanelCom.getLabel("Init", 12));
    int m = 0;
    for (int n = 0; n < this.list.size(); n++)
    {
      String str = (String)((BasicPanelCom)this.list.get(n)).getConfigValue("COM_LABEL");
      localDimension = BasicPanelCom.caculateMaxDimention(localDimension, BasicPanelCom.getLabel(str, 12));
      m += 31;
      if (((BasicPanelCom)this.list.get(n)).getIsScaleControl())
        m += 279;
    }
    if (localDimension != null)
      if (m > 500)
        this.collector.setPreferredSize(new Dimension(BasicPanelCom.getcrollPanelLength(localDimension) - 20, m));
      else
        this.collector.setPreferredSize(new Dimension(BasicPanelCom.getcrollPanelLength(localDimension), m));
    if ((this.list != null) && (this.list.size() > 0))
    {
      n = 1;
      for (int i1 = 0; i1 < this.list.size(); i1++)
      {
        Color localColor;
        if (n != 0)
          localColor = new Color(255, 255, 255);
        else
          localColor = new Color(221, 221, 227);
        if (((BasicPanelCom)this.list.get(i1)).getIsScaleControl())
        {
          if (this.scaleControlHolder == null)
            this.scaleControlHolder = new HashMap();
          CPG_ScalesActivity23 localCPG_ScalesActivity23 = new CPG_ScalesActivity23(localColor);
          localCPG_ScalesActivity23.buildView();
          localCPG_ScalesActivity23.setDefaultConfigValue();
          setScaleControl(localCPG_ScalesActivity23);
          localCPG_ScalesActivity23.setInputItem("foundationObject", this.foundationObject);
          localCPG_ScalesActivity23.setInputItem("processStep", this.processStep);
          Panel localPanel2 = new Panel();
          localPanel2.add(localCPG_ScalesActivity23);
          this.scaleControlHolder.put(((BasicPanelCom)this.list.get(i1)).getName(), localCPG_ScalesActivity23);
          try
          {
            setPanel(localPanel2, (BasicPanelCom)this.list.get(i1), new StringBuilder().append("Item ").append(i1).toString(), localDimension, localColor);
            this.collector.add(((BasicPanelCom)this.list.get(i1)).getPanel());
          }
          catch (Exception localException3)
          {
            localException3.printStackTrace();
          }
          if (n != 0)
            n = 0;
          else
            n = 1;
        }
        else
        {
          if (m > 500)
            ((BasicPanelCom)this.list.get(i1)).setPanelWidth(780);
          else
            ((BasicPanelCom)this.list.get(i1)).setPanelWidth(800);
          setPanel(null, (BasicPanelCom)this.list.get(i1), new StringBuilder().append("Item ").append(i1).toString(), localDimension, localColor);
          this.collector.add(((BasicPanelCom)this.list.get(i1)).getPanel());
          if (n != 0)
            n = 0;
          else
            n = 1;
        }
      }
    }
    this.scrollPane1.add(this.collector);
    add(this.scrollPane1);
  }

  private void setScaleControl(CPG_ScalesActivity23 paramCPG_ScalesActivity23)
  {
    setIndividualScaleControlConfigItems(paramCPG_ScalesActivity23);
  }

  private void onCollectBtClicked()
  {
    this.isCollectClick = true;
    complete();
  }

  private void onCancelBtClicked()
  {
    this.list = null;
    this.isCancelClick = true;
    complete();
  }

  private void collectingData(ActivityResult paramActivityResult)
  {
    if ((this.list == null) || (this.list.size() == 0))
    {
      setOutputItem("Result", Boolean.TRUE);
      return;
    }
    HashMap localHashMap = new HashMap();
    int i = new Long(this.config.getDataOwner()).intValue();
    Object localObject1 = null;
    if (i == 3L)
      localObject1 = this.foundationObject;
    else if (i == 1L)
      localObject1 = getRecipe(this.processStep);
    else
      localObject1 = this.processStep.getProcessStepControlRecipe().getProcessOrderItem().getParent();
    this.dcs = ((Keyed)localObject1).getDCS(this.dcsName);
    this.ins = null;
    for (int j = 0; j < this.list.size(); j++)
    {
      Object localObject2;
      if (((BasicPanelCom)this.list.get(j)).getIsScaleControl())
      {
        localObject3 = ((CPG_ScalesActivity23)this.scaleControlHolder.get(((BasicPanelCom)this.list.get(j)).getName())).activityExecute();
        if (((Response)localObject3).isError())
          paramActivityResult.setSingleError(new Error(new Exception(new StringBuilder().append("Can not get value for:").append(((BasicPanelCom)this.list.get(j)).getName()).toString()), getServer()));
        localObject2 = ((CPG_ScalesActivity23)this.scaleControlHolder.get(((BasicPanelCom)this.list.get(j)).getName())).getOutputItem("weighedValue");
      }
      else
      {
        localObject2 = ((BasicPanelCom)this.list.get(j)).getUserInput();
        if (localObject2 == null)
          continue;
        if ((localObject2 instanceof String))
        {
          if (((String)localObject2).length() == 0)
            continue;
          if ((((BasicPanelCom)this.list.get(j)).getComType() == 4L) && (((String)localObject2).length() > this.dcs.getDataCollectionItem(((BasicPanelCom)this.list.get(j)).getName()).getTextLength()))
          {
            getFunctions().dialogError(new StringBuilder().append("The length of the item [").append(((BasicPanelCom)this.list.get(j)).getName()).append("] is over the limitation!").toString());
            paramActivityResult.setSingleError(new Error(new Exception(new StringBuilder().append("The length of the item: [").append(((BasicPanelCom)this.list.get(j)).getName()).append("] is over the limitation!").toString()), getServer()));
            this.ins = null;
            return;
          }
        }
      }
      if (this.ins == null)
        this.ins = this.dcs.createDCInstance();
      Object localObject3 = (String)((BasicPanelCom)this.list.get(j)).getConfigValue("COM_LABEL");
      try
      {
        this.ins.setValue((String)localObject3, localObject2);
      }
      catch (Exception localException)
      {
        String str = new StringBuilder().append("The item [").append((String)localObject3).append("]: ").append(localException.getMessage()).toString();
        getFunctions().dialogError(str);
        paramActivityResult.setSingleError(new Error(new Exception(str), getServer()));
        this.ins = null;
        return;
      }
      localHashMap.put(localObject3, localObject2);
    }
    if ((this.config.getSubroutineName() != null) && (!invokeSubroutine(this.config.getSubroutineName(), localHashMap)))
    {
      setOutputItem("Result", Boolean.FALSE);
      paramActivityResult.setSingleError(new Error(new Exception("Can not pass the subRoutine"), getServer()));
    }
    setOutputItem("Collected Info", localHashMap);
  }

  public Response activityExecute()
  {
    if ((this.list == null) || (this.isCancelClick))
    {
      if ((this.list != null) && (this.list.size() > 0))
        for (int i = 0; i < this.list.size(); i++)
          if ((((BasicPanelCom)this.list.get(i)).getIsScaleControl()) && (this.scaleControlHolder != null))
            ((CPG_ScalesActivity23)this.scaleControlHolder.get(((BasicPanelCom)this.list.get(i)).getName())).stopPermanentWeighing();
      remove(this.scrollPane1);
      remove(this.pnlToolbar);
      remove(this.poiObjectBinder);
      return getFunctions().createResponseObject(null);
    }
    if ((!this.isCancelClick) && (!this.isCollectClick))
      return getFunctions().createResponseObject(null);
    ActivityResult localActivityResult = new ActivityResult();
    UserTransaction localUserTransaction = UserTransactionHelper.getUserTransaction(getServer());
    int j = 1;
    Response localResponse1 = new Response();
    String str = (String)getConfigurationItem("ApplicationBusinessLogicSubroutine");
    try
    {
      Object localObject1 = this.inputErrorBuffer.toString();
      if (((String)localObject1).length() > 0)
      {
        localActivityResult.setSingleError(new Error(new Exception((String)localObject1), getServer()));
        j = 0;
      }
      else
      {
        collectingData(localActivityResult);
        Object localObject2;
        Response localResponse3;
        if (this.ins == null)
        {
          localObject2 = getFunctions().createResponseObject(null);
          return localObject2;
        }
        if ((localActivityResult.getErrors() != null) && (localActivityResult.getErrors().length > 0))
        {
          j = 0;
        }
        else if ((str == null) && (this.ins != null))
        {
          localObject2 = this.dcs.save();
          if (((Response)localObject2).isError())
          {
            j = 0;
            localActivityResult.setSingleError(localObject2.getErrors()[0]);
          }
        }
        if ((j != 0) && (str != null) && (isSubroutineValid(str)))
        {
          localObject2 = getFunctions().subroutine(str, this.processStep, this.foundationObject, this.dcs, this.ins);
          if ((localObject2 != null) && ((localObject2 instanceof Response)))
          {
            localResponse1 = (Response)localObject2;
            if (localResponse1.isError())
            {
              j = 0;
              localActivityResult.setSingleError(localResponse1.getErrors()[0]);
              getFunctions().dialogError(localResponse1.getFirstErrorMessage());
            }
            else
            {
              localResponse3 = this.dcs.save();
              if (localResponse3.isError())
              {
                j = 0;
                localActivityResult.setSingleError(localResponse3.getErrors()[0]);
              }
            }
          }
          else
          {
            j = 0;
            EventLog.writeError(new StringBuilder().append(str).append(" returned invalid response.").toString());
            getFunctions().dialogError(new StringBuilder().append(str).append(" returned invalid response.").toString());
          }
        }
      }
      if (j != 0)
      {
        setOutputItem("Result", Boolean.TRUE);
      }
      else
      {
        setOutputItem("Result", Boolean.FALSE);
        setOutputItem("Collected Info", null);
      }
      setOutputItem("activityExecuteResult", localActivityResult);
      localObject1 = UserTransactionHelper.complete(localUserTransaction, localResponse1);
      if (((Response)localObject1).isError())
      {
        setOutputItem("Result", Boolean.FALSE);
        setOutputItem("Collected Info", null);
      }
    }
    catch (Exception localException1)
    {
      EventLog.logException(localException1, this, "DCSValuesCollector.activityExecute");
      j = 0;
    }
    finally
    {
      Response localResponse2;
      if (j != 0)
      {
        setOutputItem("Result", Boolean.TRUE);
      }
      else
      {
        setOutputItem("Result", Boolean.FALSE);
        setOutputItem("Collected Info", null);
      }
      setOutputItem("activityExecuteResult", localActivityResult);
      Response localResponse4 = UserTransactionHelper.complete(localUserTransaction, localResponse1);
      if (localResponse4.isError())
      {
        setOutputItem("Result", Boolean.FALSE);
        setOutputItem("Collected Info", null);
      }
    }
    if ((this.list != null) && (this.isCollectClick))
    {
      if (this.list.size() > 0)
        for (int k = 0; k < this.list.size(); k++)
          if ((((BasicPanelCom)this.list.get(k)).getIsScaleControl()) && (this.scaleControlHolder != null))
          {
            try
            {
              ((CPG_ScalesActivity23)this.scaleControlHolder.get(((BasicPanelCom)this.list.get(k)).getName())).setScaleOnline();
            }
            catch (Exception localException2)
            {
              localException2.printStackTrace();
            }
            ((CPG_ScalesActivity23)this.scaleControlHolder.get(((BasicPanelCom)this.list.get(k)).getName())).stopPermanentWeighing();
          }
      remove(this.scrollPane1);
      remove(this.pnlToolbar);
      remove(this.poiObjectBinder);
    }
    return getFunctions().createResponseObject(null);
  }

  protected void configurationItemSet(String paramString, Object paramObject)
  {
  }

  protected void configurationLoaded()
  {
  }

  public String getActivityDescription()
  {
    return new StringBuilder().append(CPGVersionInfoHelper.getVersionInfo()).append("- DCS Value Collector").toString();
  }

  protected String[] getActivityEvents()
  {
    return null;
  }

  public ItemDescriptor[] inputDescriptors()
  {
    if (this.inputDescriptors == null)
      this.inputDescriptors = new ItemDescriptor[] { ItemDescriptor.createItemDescriptor(DCSValuesCollector23.class, "foundationObject", Keyed.class, new Object[] { "shortDescription", "The Foundation Object (required)" }), ItemDescriptor.createItemDescriptor(CPG_ManualConsumption23.class, "processStep", ProcessStep.class, new Object[] { "shortDescription", "ProcessStep (required)" }) };
    return this.inputDescriptors;
  }

  protected void inputItemSet(String paramString, Object paramObject)
  {
    if ("processStep".equalsIgnoreCase(paramString))
    {
      if ((paramObject != null) && ((paramObject instanceof ProcessStep)))
      {
        this.processStep = ((ProcessStep)paramObject);
        System.out.println(new StringBuilder().append("processStep inputItemSet ").append(this.processStep.getRouteStep().getName()).toString());
      }
      else
      {
        this.inputErrorBuffer.append(new StringBuilder().append("Illegal processStep: ").append(paramObject == null ? "NULL" : paramObject.getClass().getName()).toString());
      }
    }
    else if ("foundationObject".equalsIgnoreCase(paramString))
      if ((paramObject != null) && ((paramObject instanceof Keyed)))
        this.foundationObject = ((Keyed)paramObject);
      else
        this.inputErrorBuffer.append(new StringBuilder().append("Illegal foundationObject: ").append(paramObject == null ? "NULL" : paramObject.getClass().getName()).toString());
  }

  public ItemDescriptor[] outputDescriptors()
  {
    if (this.outputDescriptors == null)
      this.outputDescriptors = new ItemDescriptor[] { ItemDescriptor.createItemDescriptor(getClass(), "Collected Info", HashMap.class, new Object[] { "shortDescription", "The collection of the data" }), ItemDescriptor.createItemDescriptor(getClass(), "Result", Boolean.class, new Object[] { "shortDescription", "Result of the collection" }) };
    return this.outputDescriptors;
  }

  protected void shutdown()
  {
  }

  protected void startup()
  {
  }

  protected void updateAfterExecute()
  {
  }

  private void setDefaultConfigValue()
  {
    setConfigurationItem(this.CONFIGURATION_ITEM_CONFIGURE_OWNER, Short.valueOf((short)1));
    setConfigurationItem("ApplicationName", "Customer_Application");
    setConfigurationItem("blockKeyboard", Boolean.FALSE);
    setConfigurationItem("allowZero", Boolean.FALSE);
    setConfigurationItem("allowTare", Boolean.TRUE);
    setConfigurationItem("allowPresetTare", Boolean.FALSE);
    setConfigurationItem("allowSetNorminal", Boolean.FALSE);
    setConfigurationItem("allowAdjust", Boolean.FALSE);
    setConfigurationItem("allowCalibrate", Boolean.FALSE);
    setConfigurationItem("allowReset", Boolean.FALSE);
  }

  public ItemDescriptor[] configurationDescriptors()
  {
    Object[] arrayOfObject = { "None", "None", "", "Foundation Object", "Foundation Object", "", "Station", "Station", "" };
    if (this.configurations == null)
      this.configurations = new ItemDescriptor[] { ItemDescriptor.createItemDescriptor(getClass(), this.CONFIGURATION_ITEM_CONFIGURE_OWNER, Short.TYPE, new Object[] { "shortDescription", "Configuration Owner", "propertyEditorClass", EnumPropertyEditor.class, "enumerationValues", { "Route Step", Short.valueOf(0), null, "Foundation Object", Short.valueOf(1), null } }), ItemDescriptor.createItemDescriptor(getClass(), "scaleStatePersistance", String.class, new Object[] { "enumerationValues", arrayOfObject, "shortDescription", "Scale State Persistance" }), ItemDescriptor.createItemDescriptor(getClass(), "blockKeyboard", Boolean.class, new Object[] { "shortDescription", "Lock or unlock the scales keyboard." }), ItemDescriptor.createItemDescriptor(getClass(), "allowZero", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Zero command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowTare", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Tare command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowPresetTare", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Preset Tare command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowSetNorminal", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Set Norminal and Tolerances command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowAdjust", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Adjust command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowCalibrate", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Calibrate command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "allowReset", Boolean.class, new Object[] { "shortDescription", "Allow user Perfoms the Reset command on this scales ." }), ItemDescriptor.createItemDescriptor(getClass(), "LiveDataWebServiceURL", String.class, new Object[] { "shortDescription", "LiveData Webservice url" }), ItemDescriptor.createItemDescriptor(getClass(), "LiveDataWebServiceServerPath", String.class, new Object[] { "shortDescription", "LiveData Webservice server path" }), ItemDescriptor.createItemDescriptor(getClass(), "PLC", String.class, new Object[] { "shortDescription", "LiveData Webservice PLC" }), ItemDescriptor.createItemDescriptor(getClass(), "ApplicationBusinessLogicSubroutine", String.class, new Object[] { "shortDescription", "Application Business Logic Subroutine name(Optional)" }), ItemDescriptor.createItemDescriptor(getClass(), "scalesSubroutine", String.class, new Object[] { "shortDescription", "The name of subroutine that will be called to provide Scales information." }), ItemDescriptor.createItemDescriptor(getClass(), "ApplicationName", String.class, new Object[] { "shortDescription", "Defines the Application Name." }) };
    return this.configurations;
  }

  private void setIndividualScaleControlConfigItems(CPG_ScalesActivity23 paramCPG_ScalesActivity23)
  {
    setIndividualScaleControlConfigItem("blockKeyboard", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowPresetTare", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowSetNorminal", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowAdjust", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowZero", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowTare", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowCalibrate", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("allowReset", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("scalesSubroutine", paramCPG_ScalesActivity23);
    setIndividualScaleControlConfigItem("scaleStatePersistance", paramCPG_ScalesActivity23);
  }

  private void setIndividualScaleControlConfigItem(String paramString, CPG_ScalesActivity23 paramCPG_ScalesActivity23)
  {
    if (getConfigurationItem(paramString) != null)
      paramCPG_ScalesActivity23.setConfigurationItem(paramString, getConfigurationItem(paramString));
  }

  private void setPanel(Panel paramPanel, BasicPanelCom paramBasicPanelCom, String paramString, Dimension paramDimension, Color paramColor)
  {
    paramBasicPanelCom.init(paramPanel, paramDimension, paramColor, null);
  }

  private ArrayList<BasicPanelCom> getConfigComs(Keyed paramKeyed)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      this.config = new DataCollectorConfig(paramKeyed);
    }
    catch (Exception localException)
    {
      JOptionPane.showMessageDialog(getJComponent(), localException.getMessage());
    }
    if ((this.config == null) || (this.config.getItems() == null) || (this.config.getItems().size() == 0) || (this.config.getDataCollectionSet() == null))
      return localArrayList;
    this.dcsName = this.config.getDataCollectionSet().getName();
    if (this.config.getLDTemplate() != null)
      this.lDAdaptor.setLdTemplateName(this.config.getLDTemplate().getName());
    this.lDAdaptor.setTsName(this.config.getLDTagSet());
    if (this.config.getLdTagSetDefinition() != null)
      this.lDAdaptor.setLdTagSetDefinitionName(this.config.getLdTagSetDefinition().getName());
    for (int i = 0; i < this.config.getItems().size(); i++)
    {
      DataCollectorConfig.DCConfigItem localDCConfigItem = (DataCollectorConfig.DCConfigItem)this.config.getItems().get(i);
      BasicPanelCom localBasicPanelCom = new BasicPanelCom();
      localBasicPanelCom.setComType(localDCConfigItem.getDataType());
      HashMap localHashMap = new HashMap();
      localHashMap.put("COM_LABEL", localDCConfigItem.getName());
      localBasicPanelCom.setName(localDCConfigItem.getName());
      if (localDCConfigItem.getQuantitySource().equalsIgnoreCase("PLC"))
      {
        localBasicPanelCom.setIsPLCRead(true);
        localHashMap.put("PLC_TAG", localDCConfigItem.getTag());
        localHashMap.put("PLC_ADAPTOR", this.lDAdaptor);
      }
      else if (localDCConfigItem.getQuantitySource().equalsIgnoreCase("scale"))
      {
        localBasicPanelCom.setIsScaleControl(true);
      }
      if (localDCConfigItem.getListValues() != null)
      {
        Vector localVector = new Vector();
        for (int j = 0; j < localDCConfigItem.getListValues().getItems().size(); j++)
        {
          localVector.add(localDCConfigItem.getListValues().getItems().get(j));
          localHashMap.put("LIST_VECTOR", localVector);
        }
      }
      localBasicPanelCom.setConfig(localHashMap);
      localArrayList.add(localBasicPanelCom);
    }
    return localArrayList;
  }

  private boolean invokeSubroutine(String paramString, HashMap<String, Object> paramHashMap)
  {
    try
    {
      if (isSubroutineValid(paramString))
      {
        Object localObject = getFunctions().subroutine(paramString, this.processStep, this.foundationObject, paramHashMap);
        if ((localObject != null) && ((localObject instanceof Response)))
        {
          Response localResponse = (Response)localObject;
          getFunctions().checkAndDisplayResponse(localResponse);
          if ((localResponse != null) && (localResponse.isOk()))
            return true;
          getFunctions().dialogError(new StringBuilder().append(paramString).append(" subroutine result is invalid").toString());
        }
        else
        {
          getFunctions().dialogError(new StringBuilder().append(paramString).append(" subroutine must return a Response").toString());
        }
      }
      else
      {
        System.out.println(new StringBuilder().append("DCSValuesCollector::invokeScalesSubroutine ").append(paramString).append(" is invalid").toString());
      }
    }
    catch (Exception localException)
    {
      dislayException(localException);
    }
    return false;
  }

  private void dislayException(Exception paramException)
  {
    String str = paramException.getMessage();
    if (str == null)
    {
      str = paramException.getClass().getName();
      paramException.printStackTrace();
    }
    else
    {
      EventLog.writeError(str);
    }
    getFunctions().dialogError(str);
  }

  protected boolean isSubroutineValid(String paramString)
  {
    boolean bool = false;
    if ((paramString != null) && (paramString.trim().length() > 0))
    {
      SubroutineManager localSubroutineManager = ServerImpl.getDefaultServer().getSubroutineManager();
      try
      {
        Subroutine localSubroutine = (Subroutine)localSubroutineManager.getObject(paramString);
        if (localSubroutine != null)
          bool = true;
        else
          EventLog.writeError(new StringBuilder().append(paramString).append(" does not exist.").toString());
      }
      catch (Exception localException)
      {
        EventLog.logException(localException, this, new StringBuilder().append("isSubroutineValid ").append(paramString).toString());
        dislayException(localException);
      }
    }
    return bool;
  }

  public static class ConfigCom
  {
    public static int SMEDITOR = 0;
    public static int STRINGFIELD = 1;
    public static int SCALECONTROL = 2;
    public static int FROMPLC = 3;
    public static int CHECKBOX = 4;
    public static int LIST = 5;
    private int comType = -1;
    private Object configuredObj;

    public int getComType()
    {
      return this.comType;
    }

    public void setComType(int paramInt)
    {
      this.comType = paramInt;
    }

    public Object getConfiguredObj()
    {
      return this.configuredObj;
    }

    public void setConfiguredObj(Object paramObject)
    {
      this.configuredObj = paramObject;
    }

    public int getHigh()
    {
      if (this.comType == SCALECONTROL)
        return 300;
      return 100;
    }
  }
}

/* Location:           C:\Users\Xuyh\Desktop\YiliTmpLib\OrderMgmnt23.jar
 * Qualified Name:     com.rockwell.om.execution.manual.DCSValuesCollector23
 * JD-Core Version:    0.6.2
 */