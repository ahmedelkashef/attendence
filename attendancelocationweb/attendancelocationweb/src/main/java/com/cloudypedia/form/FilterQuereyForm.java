package com.cloudypedia.form;

import java.util.List;
/**
 * This class used to bind json array of filters to apply
 *
 */
public class FilterQuereyForm {
	
	//filters to apply
	private List<Filter> filters;
	
	/**
	 * Filter
	 *
	 */
	public static class Filter{
		//date in milliseconds
		private long dateLong;
		
		//string value of filed
		private String value;
		
		//field operator
		private String operator;
		
		//field name
		private String field;
		
		public Filter(){}
		
		public long getDateLong() {
			return dateLong;
		}
		public void setDateLong(long dateLong) {
			this.dateLong = dateLong;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}
		
	}
	
	public List<Filter> getFilters() {
		return filters;
	}
	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}
	

}
