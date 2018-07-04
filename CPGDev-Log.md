## CPG DEV Log
### 1.```RuntimeMFC```的```getType()```方法返回类型：
- 1 = Input
- 2 = Transfer
- 3 = Output
- 4 = Co-Product
- 5 = By-Product
### 2.RuntimeMFC的获取
```
    runtimeMFCs=controlRecipe.getRuntimeMFCs()
    foreach runtimeMFC(runtimeMFCs){

    }
```
### 3.FTPC对象与CPG对象之间的连接
- ```com.rockwell.om.app.utility.RecipeResourceSet```
    - Code
    ```java
    RecipeResourceSet::getRecipeResourceSet(controlRecipe)
    recipeResources=recipeResourceSet.getRecipeResources()
    ```
### 4.FormCntrl23Activity中使用Form，Form中的Edit需要设置**alternativeEditorSupportClass**
- smartEdit
- MeasuredValue ```com.datasweep.plantops.property.editor.MeasuredValuePropertyEditor```
- String
    - ```com.rockwell.om.control.ReadOnlyStringPropertyEditor```
    - ```com.datasweep.plantops.property.editor.StringPropertyEditor```
