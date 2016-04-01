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
package net.as_development.asdk.db_service.impl.backend.mongodb;

import java.util.Date;

//==============================================================================
/**
 * TODO document me
 */
public class DatatypeConvert
{
    //--------------------------------------------------------------------------
    /** create new instance.
     */
    public DatatypeConvert ()
    {}

    //--------------------------------------------------------------------------
	public static String toString (Class< ? >  aType    ,
                                   Object      aValue   ,
                                   boolean     bScramble)
        throws Exception
    {
        if (aValue == null)
            return null;

        if (aType.equals(Date.class))
            return String.valueOf(((Date)aValue).getTime());
            
        return aValue.toString ();
/*
        if (aType.equals(String.class))
            return (String)aValue;
        else
        //if (aType.equals(int.class))
            return aValue.toString ();
        //else
        //  throw new RuntimeException ("Not implemented yet. Support more data types for SimpleDB please.");
 */
    }

    //--------------------------------------------------------------------------
	public static Object fromString (Class< ? > aType    ,
                                     String     sValue   ,
                                     boolean    bScramble)
        throws Exception
    {
        if (sValue == null)
            return null;

        Object aValue = null;
        
        if (aType.equals(String.class))
            aValue = sValue;
        else
        if (
            (aType.equals(char.class))
           )
            aValue = sValue.charAt(0);
        else
        if (
            (aType.equals(byte.class)) ||
            (aType.equals(Byte.class))
           )
            aValue = Byte.parseByte(sValue);
        else
        if (
            (aType.equals(short.class)) ||
            (aType.equals(Short.class))
           )
            aValue = Short.parseShort(sValue);
        else
        if (
            (aType.equals(int.class    )) ||
            (aType.equals(Integer.class))
           )
            aValue = Integer.parseInt(sValue);
        else
        if (
            (aType.equals(long.class)) ||
            (aType.equals(Long.class))
           )
            aValue = Long.parseLong(sValue);
        else
        if (
            (aType.equals(boolean.class)) ||
            (aType.equals(Boolean.class))
           )
            aValue = Boolean.parseBoolean(sValue);
        else
        if (
            (aType.equals(float.class)) ||
            (aType.equals(Float.class))
           )
            aValue = Float.parseFloat(sValue);
        else
        if (
            (aType.equals(double.class)) ||
            (aType.equals(Double.class))
           )
            aValue = Double.parseDouble(sValue);
        else
        if (aType.equals(Date.class))
            aValue = new Date(Long.parseLong(sValue));
        else
            throw new RuntimeException ("Not implemented yet. Support data type '"+aType.getName ()+"' for MongoDB please.");
        
        return aValue;
    }
}
