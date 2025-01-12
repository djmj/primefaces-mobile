/*
 * Copyright 2009-2014 PrimeTek.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.mobile.renderkit;

import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.primefaces.component.api.UICalendar;
import org.primefaces.component.calendar.Calendar;
import org.primefaces.util.CalendarUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

public class CalendarRenderer extends org.primefaces.component.calendar.CalendarRenderer {
 
    public final static String MOBILE_POPUP_CONTAINER_CLASS = "ui-calendar ui-calendar-popup";
    public final static String MOBILE_INLINE_CONTAINER_CLASS = "ui-calendar ui-calendar-inline";
	
    @Override
    protected void encodeMarkup(FacesContext context, UICalendar uicalendar, String value) throws IOException 
    {
    	Calendar calendar = (Calendar) uicalendar;
    	
    	ResponseWriter writer = context.getResponseWriter();
        String clientId = uicalendar.getClientId(context);
        String inputId = clientId + "_input";
        String containerClass = calendar.isPopup() ? MOBILE_POPUP_CONTAINER_CLASS : MOBILE_INLINE_CONTAINER_CLASS;
        String style = uicalendar.getStyle();
        String styleClass = uicalendar.getStyleClass();
        styleClass = (styleClass == null) ? containerClass: containerClass + " " + styleClass;

        writer.startElement("div", uicalendar);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("class", styleClass, null);
        if(style != null) {
            writer.writeAttribute("style", style, null);
        }

        encodeInput(context, uicalendar, inputId, value, calendar.isPopup());
        
        writer.endElement("div");
    }
    
    protected void encodeInput(FacesContext context, Calendar calendar, String id, String value) throws IOException {
        ResponseWriter writer = context.getResponseWriter();
        
        writer.startElement("input", null);
        writer.writeAttribute("id", id, null);
        writer.writeAttribute("name", id, null);
        writer.writeAttribute("type", "text", null);

        if(!isValueBlank(value)) {
            writer.writeAttribute("value", value, null);
        }

        if(calendar.isReadonly()||calendar.isReadonlyInput()) writer.writeAttribute("readonly", "readonly", null);
        if(calendar.isDisabled()) writer.writeAttribute("disabled", "disabled", null);

        renderPassThruAttributes(context, calendar, HTML.INPUT_TEXT_ATTRS_WITHOUT_EVENTS);
        renderDomEvents(context, calendar, HTML.INPUT_TEXT_EVENTS);
        
        writer.endElement("input");
    }
    
    @Override
    protected void encodeScript(FacesContext context, UICalendar uicalendar, String value) throws IOException 
    {
    	Calendar calendar = (Calendar) uicalendar;

        Locale locale = uicalendar.calculateLocale(context);
        String pattern = uicalendar.calculatePattern();
        WidgetBuilder wb = getWidgetBuilder(context);
        wb.init("Calendar", calendar);
        
        wb.attr("popup", calendar.isPopup())
            .attr("locale", locale.toString())
            .attr("dateFormat", CalendarUtils.convertPattern(pattern));
        
        //default date
        Object pagedate = calendar.getPagedate();
        String defaultDate = null;
        
        if(uicalendar.isConversionFailed()) {
            defaultDate = CalendarUtils.getValueAsString(context, uicalendar, new Date());
        }
        else if(!isValueBlank(value)) {
            defaultDate = value;
        } 
        else if(pagedate != null) {
            defaultDate = CalendarUtils.getValueAsString(context, uicalendar, pagedate);
        }
        
        wb.attr("defaultDate", defaultDate, null)
            .attr("numberOfMonths", calendar.getPages(), 1)
            .attr("minDate", CalendarUtils.getValueAsString(context, uicalendar, uicalendar.getMindate()), null)
            .attr("maxDate", CalendarUtils.getValueAsString(context, uicalendar, uicalendar.getMaxdate()), null)
            .attr("showButtonPanel", calendar.isShowButtonPanel(), false)
            .attr("showWeek", calendar.isShowWeek(), false)
            .attr("disabledWeekends", calendar.isDisabledWeekends(), false)
            .attr("disabled", calendar.isDisabled(), false)
            .attr("yearRange", calendar.getYearRange(), null);
        
        if(calendar.isNavigator()) {
            wb.attr("changeMonth", true).attr("changeYear", true);
        }
        
        if(calendar.getEffect() != null) {
            wb.attr("showAnim", calendar.getEffect()).attr("duration", calendar.getEffectDuration());
        }
        
        String beforeShowDay = calendar.getBeforeShowDay();
        if(beforeShowDay != null) {
            wb.nativeAttr("preShowDay", beforeShowDay);
        }
        
        String showOn = calendar.getShowOn();
        if(!showOn.equalsIgnoreCase("focus")) {
            wb.attr("showOn", showOn);
        }
        
        if(calendar.isShowOtherMonths()) {
            wb.attr("showOtherMonths", true).attr("selectOtherMonths", true);
        }
                
        encodeClientBehaviors(context, uicalendar);
        
        wb.finish();
    }
}
