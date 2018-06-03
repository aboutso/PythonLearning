---
title: MF Demo——Equipment Management
tags: MF,小书匠
grammar_cjkRuby: true
---
### root package “com.rockwell.equipment”

### 1. ui
#### 1.1 class ```EquipmentAppUI```
##### 注解
```
@Widgetset(IApplicationConstants.WIDGETSET)
@Theme(IEquipmentConstants.EQUIPMENT_THEME)
@CDIUI(IEquipmentConstants.EQUIPMENT_UI)
@PreserveOnRefresh
```
##### Inject
```
private CDIViewProvider viewProvider;

protected IApplicationNavBar appNavBar;

private IEquipmentNavBar equipmentNavBar;
```