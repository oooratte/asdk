/**
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <http://unlicense.org/>
 */
package net.as_development.asdk.tools.common.datetime;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtils
{
	//-------------------------------------------------------------------------
	public static Date setHours (final Date aDate ,
								 final int  nValue)
	    throws Exception
	{
		return impl_set (aDate, Calendar.HOUR_OF_DAY, nValue);
	}
	
	//-------------------------------------------------------------------------
	public static Date setMinutes (final Date aDate ,
								   final int  nValue)
	    throws Exception
	{
		return impl_set (aDate, Calendar.MINUTE, nValue);
	}

	//-------------------------------------------------------------------------
	public static Date setSeconds (final Date aDate ,
								   final int  nValue)
	    throws Exception
	{
		return impl_set (aDate, Calendar.SECOND, nValue);
	}

	//-------------------------------------------------------------------------
	public static int getHours (final Date aDate)
	    throws Exception
	{
		return impl_get (aDate, Calendar.HOUR_OF_DAY);
	}

	//-------------------------------------------------------------------------
	public static int getMinutes (final Date aDate)
	    throws Exception
	{
		return impl_get (aDate, Calendar.MINUTE);
	}

	//-------------------------------------------------------------------------
	public static int getSeconds (final Date aDate)
	    throws Exception
	{
		return impl_get (aDate, Calendar.SECOND);
	}

	//-------------------------------------------------------------------------
	public static Time getTime4Now ()
	    throws Exception
	{
		final Date aNow  = new Date ();
		final Time aTime = new Time (aNow.getTime());
		return aTime;
	}
	
	//-------------------------------------------------------------------------
	public static Date getDate4Now ()
	    throws Exception
	{
		final Date aNow = new Date ();
		return aNow;
	}

	//-------------------------------------------------------------------------
	public static Time addTime (final Time aTime    ,
								final long nTime2Add)
	    throws Exception
	{
		      long nResult  = aTime.getTime();
		           nResult += nTime2Add;
		final Time aResult  = new Time (nResult);
		return aResult;
	}

	//-------------------------------------------------------------------------
	public static Date setTimeOnDate (final Date aDate,
								      final Time aTime)
	    throws Exception
	{
		final int  nHours   = DateTimeUtils.getHours  (aTime);
		final int  nMinutes = DateTimeUtils.getMinutes(aTime);
		final int  nSeconds = DateTimeUtils.getSeconds(aTime);
		
			  Date aResult  = aDate;
		      	   aResult  = DateTimeUtils.setHours  (aResult, nHours  );
		      	   aResult  = DateTimeUtils.setMinutes(aResult, nMinutes);
		      	   aResult  = DateTimeUtils.setSeconds(aResult, nSeconds);
		
		return aResult;
	}

	//-------------------------------------------------------------------------
    private static Date impl_set(final Date aDate         ,
    							 final int  nCalendarField,
    							 final int  nValue        )
    	throws Exception
    {
        final Calendar aCal = Calendar.getInstance();
        aCal.setLenient(false                 );
        aCal.setTime   (aDate                 );
        aCal.set       (nCalendarField, nValue);
        return aCal.getTime();
    }

	//-------------------------------------------------------------------------
    private static int impl_get(final Date aDate         ,
    							final int  nCalendarField)
    	throws Exception
    {
        final Calendar aCal = Calendar.getInstance();
        aCal.setLenient(false);
        aCal.setTime   (aDate);
        
        final int nValue = aCal.get (nCalendarField);
        return nValue;
    }
}
