package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;
import org.sigmah.shared.dto.referential.ValueEventChangeType;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ValueJS extends JavaScriptObject {
	
	protected ValueJS() {
	}
	
	public static ValueJS toJavaScript(GetValue getValue, ValueResult valueResult) {
		final ValueJS valueJS = Values.createJavaScriptObject(ValueJS.class);
		
		valueJS.setId(ValueJSIdentifierFactory.toIdentifier(getValue));
		valueJS.setElementEntityName(getValue.getElementEntityName());
		valueJS.setProjectId(getValue.getProjectId());
		valueJS.setElementId(getValue.getElementId());
		valueJS.setAmendmentId(getValue.getAmendmentId());
		
		valueJS.setValue(valueResult.getValueObject());
		valueJS.setValues(valueResult.getValuesObject());
		valueJS.setAmendment(valueResult.isAmendment());
		
		return valueJS;
	}
	
	public static ValueJS toJavaScript(UpdateProject updateProject, ValueEventWrapper valueEventWrapper) {
		return toJavaScript(updateProject, valueEventWrapper, null);
	}
	
	public static ValueJS toJavaScript(UpdateProject updateProject, ValueEventWrapper valueEventWrapper, ValueResult originalValue) {
		final ValueJS valueJS = Values.createJavaScriptObject(ValueJS.class);
		
		valueJS.setId(ValueJSIdentifierFactory.toIdentifier(updateProject, valueEventWrapper));
		valueJS.setElementEntityName(valueEventWrapper.getSourceElement().getEntityName());
		valueJS.setProjectId(updateProject.getProjectId());
		valueJS.setElementId(valueEventWrapper.getSourceElement().getId());
		valueJS.setAmendmentId(null);
		
		if(originalValue != null) {
			valueJS.setValue(originalValue.getValueObject());
			valueJS.setValues(originalValue.getValuesObject());
			valueJS.setAmendment(originalValue.isAmendment());
		}
		
		valueJS.update(valueEventWrapper);
		
		valueJS.setChangeType(valueEventWrapper.getChangeType());
		valueJS.setProjectCountryChanged(valueEventWrapper.isProjectCountryChanged());
		valueJS.setSourceElement(FlexibleElementJS.toJavaScript(valueEventWrapper.getSourceElement()));
		
		return valueJS;
	}
	
	public ValueResult toValueResult() {
		final ValueResult valueResult = new ValueResult();
		
		valueResult.setAmendment(isAmendment());
		valueResult.setValueObject(getValue());
		
		final JsArray<ListableValueJS> values = getValues();
		if(values != null) {
			final ArrayList<ListableValue> list = new ArrayList<ListableValue>();
			
			for(int index = 0; index < values.length(); index++) {
				list.add(values.get(index).toDTO());
			}
			
			valueResult.setValuesObject(list);
		}
		
		return valueResult;
	}
	
	public ValueEventWrapper toValueEventWrapper() {
		final ValueEventWrapper valueEventWrapper = new ValueEventWrapper();
		
		valueEventWrapper.setChangeType(getChangeTypeEnum());
		valueEventWrapper.setProjectCountryChanged(isProjectCountryChanged());
		valueEventWrapper.setSingleValue(getValue());
		
		if(getValues() != null && getValues().length() == 1) {
			final ListableValue listableValue = getValues().get(0).toDTO();
			if(listableValue instanceof TripletValueDTO) {
				valueEventWrapper.setListValue((TripletValueDTO)listableValue);
			}
		}
		
		if(getSourceElement() != null) {
			valueEventWrapper.setSourceElement(getSourceElement().toDTO());
		}
		
		return valueEventWrapper;
	}
	
	public void update(ValueEventWrapper valueEventWrapper) {
		setValue(valueEventWrapper.getSingleValue());
		
		if(valueEventWrapper.getListValue() instanceof ListableValue) {
			JsArray<ListableValueJS> values = getValues();
			if(values == null) {
				values = (JsArray<ListableValueJS>) JavaScriptObject.createArray();
				setValues(values);
			}
			
			switch(valueEventWrapper.getChangeType()) {
				case ADD:
					values.push(ListableValueJS.toJavaScript(valueEventWrapper.getListValue()));
					break;
					
				case REMOVE:
					final JsArray<ListableValueJS> trimmedArray = (JsArray<ListableValueJS>) JavaScriptObject.createArray();
					
					for(int index = 0; index < values.length(); index++) {
						final ListableValueJS entry = values.get(index);
						if(entry.getId() != valueEventWrapper.getListValue().getId()) {
							trimmedArray.push(entry);
						} else {
							Log.info("Removing entry " + entry.getId() + " from " + getId());
						}
					}
					
					setValues(trimmedArray);
					break;
					
				default:
					break;
			}
		}
	}
	
	public native String getId() /*-{
		return this.id;
	}-*/;

	public native void setId(String id) /*-{
		this.id = id;
	}-*/;
	
	public native String getElementEntityName() /*-{
		return this.elementEntityName;
	}-*/;

	public native void setElementEntityName(String elementEntityName) /*-{
		this.elementEntityName = elementEntityName;
	}-*/;

	public native int getProjectId() /*-{
		return this.projectId;
	}-*/;

	public native void setProjectId(int projectId) /*-{
		this.projectId = projectId;
	}-*/;

	public native int getElementId() /*-{
		return this.elementId;
	}-*/;

	public native void setElementId(int elementId) /*-{
		this.elementId = elementId;
	}-*/;

	public native int getAmendmentId() /*-{
		return this.amendmentId;
	}-*/;

	public void setAmendmentId(Integer amendmentId) {
		if(amendmentId != null) {
			setAmendmentId(amendmentId.intValue());
		}
	}
	
	public native void setAmendmentId(int amendmentId) /*-{
		this.amendmentId = amendmentId;
	}-*/;

	public native String getValue() /*-{
		return this.value;
	}-*/;

	public native void setValue(String value) /*-{
		this.value = value;
	}-*/;

	public native <T extends ListableValueJS> JsArray<T> getValues() /*-{
		return this.values;
	}-*/;

	public void setValues(List<ListableValue> values) {
		if(values != null) {
			final JsArray<ListableValueJS> array = Values.createJavaScriptArray(JsArray.class);
			
			for(final ListableValue value : values) {
				array.push(ListableValueJS.toJavaScript(value));
			}
			
			setValues(array);
		}
	}
	
	public native void setValues(JsArray<ListableValueJS> values) /*-{
		this.values = values;
	}-*/;
	
	public native boolean isAmendment() /*-{
		return this.amendment;
	}-*/;
	
	public native void setAmendment(boolean amendment) /*-{
		this.amendment = amendment;
	}-*/;
	
	public native String getChangeType() /*-{
		return this.changeType;
	}-*/;
	
	public ValueEventChangeType getChangeTypeEnum() {
		if(getChangeType() != null) {
			return ValueEventChangeType.valueOf(getChangeType());
		}
		return null;
	}

	public void setChangeType(ValueEventChangeType changeType) {
		if(changeType != null) {
			setChangeType(changeType.name());
		}
	}
	
	public native void setChangeType(String changeType) /*-{
		this.changeType = changeType;
	}-*/;
	
	public native boolean isProjectCountryChanged() /*-{
		return this.projectCountryChanged;
	}-*/;
	
	public native void setProjectCountryChanged(boolean projectCountryChanged) /*-{
		this.projectCountryChanged = projectCountryChanged;
	}-*/;
	
	public native FlexibleElementJS getSourceElement() /*-{
		return this.sourceElement;
	}-*/;

	public void setSourceElement(FlexibleElementDTO sourceElement) {
		if(sourceElement != null) {
			setSourceElement(FlexibleElementJS.toJavaScript(sourceElement));
		}
	}
	
	public native void setSourceElement(FlexibleElementJS sourceElement) /*-{
		this.sourceElement = sourceElement;
	}-*/;
}
