package org.sigmah.shared.dto.pivot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.sigmah.shared.dto.referential.DimensionType;

import com.extjs.gxt.ui.client.data.BaseModelData;
import org.sigmah.server.report.model.adapter.DimensionAdapter;
import org.sigmah.shared.dto.pivot.content.CategoryProperties;
import org.sigmah.shared.dto.pivot.content.DimensionCategory;
import org.sigmah.shared.dto.pivot.content.EntityCategory;

/**
 * @author Alex Bertram (v1.3)
 */
@XmlJavaTypeAdapter(DimensionAdapter.class)
public class Dimension extends BaseModelData implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1062078302199897126L;

	public final static String CAPTION_PROPERTY = "caption";
	private DimensionType type;
	private String color;

	private Map<DimensionCategory, CategoryProperties> categories = new HashMap<DimensionCategory, CategoryProperties>(0);

	private List<DimensionCategory> ordering = new ArrayList<DimensionCategory>();

	/**
	 * Required for GWT serialization
	 */
	protected Dimension() {
	}

	public Dimension(DimensionType type) {
		this.type = type;
	}

	public Dimension(String caption, DimensionType type) {
		this.type = type;
		set("caption", caption);
		set("id", "dim_" + type);
	}

	public DimensionType getType() {
		return type;
	}

	@SuppressWarnings("unused")
	private void setType(DimensionType type) {
		this.type = type;
	}

	/**
	 * @return The <i>type</i> of order applied to this dimension
	 */
	@XmlTransient
	public boolean isOrderDefined() {
		return !ordering.isEmpty();
	}

	/**
	 * @return The model-supplied (i.e. specified in the XML) category order of this dimension.
	 */
	@XmlTransient
	public List<DimensionCategory> getOrdering() {
		return ordering;
	}

	@SuppressWarnings("unused")
	private void setOrdering(List<DimensionCategory> ordering) {
		this.ordering = ordering;
	}

	/**
	 * @param category
	 * @return The model-supplied (i.e. specified in the XML) category label for a given category in this dimension
	 */
	public String getLabel(DimensionCategory category) {
		CategoryProperties props = categories.get(category);
		return props == null ? null : props.getLabel();
	}

	@XmlTransient
	public Map<DimensionCategory, CategoryProperties> getCategories() {
		return categories;
	}

	public void setCategories(Map<DimensionCategory, CategoryProperties> categories) {
		this.categories = categories;
	}

	/**
	 * Adds a model supplied label for a given category in this dimension
	 *
	 * @param category
	 * @param props
	 */
	public void setProperties(DimensionCategory category, CategoryProperties props) {
		categories.put(category, props);
	}

	public void setProperties(int id, CategoryProperties props) {
		categories.put(new EntityCategory(id), props);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof Dimension)) {
			return false;
		}

		Dimension that = (Dimension) other;

		return this.type == that.type;

	}

	@Override
	public int hashCode() {
		return this.type.hashCode();
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public void setCategoryColor(int id, int color) {
		EntityCategory cat = new EntityCategory(id);
		CategoryProperties props = categories.get(cat);
		if (props == null) {
			props = new CategoryProperties();
			categories.put(cat, props);
		}
		props.setColor(color);
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
