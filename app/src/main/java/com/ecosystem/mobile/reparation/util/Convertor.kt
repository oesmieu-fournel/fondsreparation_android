package com.ecosystem.mobile.reparation.util

import com.sap.cloud.mobile.odata.*
import java.nio.charset.StandardCharsets

object Converter {
    sealed interface ConvertResult<out T> {
        data class ConvertSuccess<T>(val data: T) : ConvertResult<T>
        data class ConvertError(val exception: Throwable? = null) : ConvertResult<Nothing>
    }

    fun populateEntityWithPropertyValue(
        existingEntity: EntityValue,
        propertyValuePairs: List<Pair<Property, String>>
    ): List<ConvertResult.ConvertError> {
        val convertErrors = mutableListOf<ConvertResult.ConvertError>()
        for (propertyValPair in propertyValuePairs) {
            val (property, value) = propertyValPair
//            val oldValue = existingEntity.getDataValue(property)
            if (value.isNotEmpty()) {
                val convertResult = convert(property, value)
                if (convertResult is ConvertResult.ConvertSuccess) {
                    existingEntity.setDataValue(property, convertResult.data)
                } else {
                    convertErrors.add(convertResult as ConvertResult.ConvertError)
                }
            } else {
                if (!property.isNullable) {
                    convertErrors.add(ConvertResult.ConvertError(IllegalArgumentException("Mandatory field empty")))
                }
            }

        }
        return convertErrors
    }

    fun convert(
        property: Property,
        value: String
    ): ConvertResult<DataValue> {
        return when (property.type) {
            BasicType.INT -> toInt(value)
            BasicType.DECIMAL -> toDecimal(value)
            BasicType.INTEGER -> toBigInteger(value)
            BasicType.STRING -> toString(value)
            BasicType.LONG -> toLong(value)
            BasicType.SHORT -> toShort(value)
            BasicType.BYTE -> toByte(value)
            BasicType.BINARY -> toByteArray(value)
            BasicType.DOUBLE -> toDouble(value)
            BasicType.FLOAT -> toFloat(value)
            BasicType.BOOLEAN -> toBoolean(value)
            BasicType.LOCAL_DATE_TIME -> toLocalDateTime(value)
            BasicType.GLOBAL_DATE_TIME -> toGlobalDateTime(value)
            BasicType.LOCAL_DATE -> toLocalDate(value)
            BasicType.LOCAL_TIME -> toLocalTime(value)
            BasicType.DAY_TIME_DURATION -> toDayTimeDuration(value)
            BasicType.GUID_VALUE -> toGuidValue(value)
            BasicType.GEOGRAPHY_POINT -> toGeographyPoint(value)
            BasicType.GEOGRAPHY_COLLECTION -> toGeographyCollection(value)
            BasicType.GEOGRAPHY_MULTI_POINT -> toGeographyMultiPoint(value)
            BasicType.GEOGRAPHY_LINE_STRING -> toGeographyLineString(value)
            BasicType.GEOGRAPHY_MULTI_LINE_STRING -> toGeographyMultiLineString(value)
            BasicType.GEOGRAPHY_POLYGON -> toGeographyPolygon(value)
            BasicType.GEOGRAPHY_MULTI_POLYGON -> toGeographyMultiPolygon(value)
            BasicType.GEOMETRY_POINT -> toGeometryPoint(value)
            BasicType.GEOMETRY_COLLECTION -> toGeometryCollection(value)
            BasicType.GEOGRAPHY_MULTI_POINT -> toGeometryMultiPoint(value)
            BasicType.GEOMETRY_LINE_STRING -> toGeometryLineString(value)
            BasicType.GEOGRAPHY_MULTI_LINE_STRING -> toGeometryMultiLineString(value)
            BasicType.GEOMETRY_POLYGON -> toGeometryPolygon(value)
            BasicType.GEOGRAPHY_MULTI_POLYGON -> toGeometryMultiPolygon(value)

            else -> ConvertResult.ConvertError(UnsupportedOperationException())
        }
    }

    fun toString(value: String): ConvertResult<StringValue> {
        return ConvertResult.ConvertSuccess(StringValue.of(value))
    }

    fun toInt(value: String): ConvertResult<IntValue> {
        return try {
            ConvertResult.ConvertSuccess(IntValue.of(value.toInt()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    fun toBigInteger(value: String): ConvertResult<IntegerValue> {
        return try {
            ConvertResult.ConvertSuccess(IntegerValue.of(value.toBigInteger()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }

    }

    /*
     * For OData types: Edm.Int64
     */
    fun toLong(value: String): ConvertResult<LongValue> {
        return try {
            ConvertResult.ConvertSuccess(LongValue.of(value.toLong()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }

    }

    /*
     * For OData types: Edm.Int16
     */
    fun toShort(value: String): ConvertResult<ShortValue> {
        return try {
            ConvertResult.ConvertSuccess(ShortValue.of(value.toShort()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.SByte
     */
    fun toByte(value: String): ConvertResult<ByteValue> {
        return try {
            ConvertResult.ConvertSuccess(ByteValue.of(value.toByte()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.Decimal
     */
    @JvmStatic
    fun toDecimal(value: String): ConvertResult<DecimalValue> {
        return try {
            ConvertResult.ConvertSuccess(DecimalValue.of(value.toBigDecimal()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.Binary
     */
    fun toByteArray(value: String): ConvertResult<BinaryValue> {
        return try {
            ConvertResult.ConvertSuccess(BinaryValue.of(value.toByteArray(StandardCharsets.UTF_8)))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.Double
     */
    fun toDouble(value: String): ConvertResult<DoubleValue> {
        return try {
            ConvertResult.ConvertSuccess(DoubleValue.of(value.toDouble()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.Single
     */
    fun toFloat(value: String): ConvertResult<FloatValue> {
        return try {
            ConvertResult.ConvertSuccess(FloatValue.of(value.toFloat()))
        } catch (e: NumberFormatException) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.DateTime (V2 only)
     */
    fun toLocalDateTime(value: String): ConvertResult<LocalDateTime> {
        val result = LocalDateTime.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.DateTimeOffset
     */
    fun toGlobalDateTime(value: String): ConvertResult<GlobalDateTime> {
        val result = GlobalDateTime.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.Date
     */
    fun toLocalDate(value: String): ConvertResult<LocalDate> {
        val result = LocalDate.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.TimeOfDay
     */
    fun toLocalTime(value: String): ConvertResult<LocalTime> {
        val result = LocalTime.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.TimeOfDay
     */
    fun toDayTimeDuration(value: String): ConvertResult<DayTimeDuration> {
        val result = DayTimeDuration.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.Guid
     */
    fun toGuidValue(value: String): ConvertResult<GuidValue> {
        val result = GuidValue.parse(value)
        return if (result == null) {
            ConvertResult.ConvertError(NumberFormatException())
        } else {
            ConvertResult.ConvertSuccess(result)
        }
    }

    /*
     * For OData types: Edm.Boolean
     */
    fun toBoolean(value: String): ConvertResult<BooleanValue> =
        ConvertResult.ConvertSuccess(BooleanValue.of(value.toBoolean()))

    /*
     * For OData types: Edm.GeographyPoint
     */
    fun toGeographyPoint(
        value: String
    ): ConvertResult<GeographyPoint> {
        return try {
            ConvertResult.ConvertSuccess(GeographyPoint.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyCollection
     */
    fun toGeographyCollection(
        value: String
    ): ConvertResult<GeographyCollection> {
        return try {
            ConvertResult.ConvertSuccess(GeographyCollection.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyMultiPoint
     */
    fun toGeographyMultiPoint(
        value: String
    ): ConvertResult<GeographyMultiPoint> {
        return try {
            ConvertResult.ConvertSuccess(GeographyMultiPoint.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyMultiPoint
     */
    @JvmStatic
    fun toGeographyLineString(
        value: String
    ): ConvertResult<GeographyLineString> {
        return try {
            ConvertResult.ConvertSuccess(GeographyLineString.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyMultiLineString
     */
    @JvmStatic
    fun toGeographyMultiLineString(
        value: String
    ): ConvertResult<GeographyMultiLineString> {
        return try {
            ConvertResult.ConvertSuccess(GeographyMultiLineString.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyPolygon
     */
    @JvmStatic
    fun toGeographyPolygon(
        value: String
    ): ConvertResult<GeographyPolygon> {
        return try {
            ConvertResult.ConvertSuccess(GeographyPolygon.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeographyMultiPolygon
     */
    @JvmStatic
    fun toGeographyMultiPolygon(
        value: String
    ): ConvertResult<GeographyMultiPolygon> {
        return try {
            ConvertResult.ConvertSuccess(GeographyMultiPolygon.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryPoint
     */
    @JvmStatic
    fun toGeometryPoint(
        value: String
    ): ConvertResult<GeometryPoint> {
        return try {
            ConvertResult.ConvertSuccess(GeometryPoint.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryCollection
     * Handles two way data binding to and from GeometryCollection
     */
    @JvmStatic
    fun toGeometryCollection(
        value: String
    ): ConvertResult<GeometryCollection> {
        return try {
            ConvertResult.ConvertSuccess(GeometryCollection.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryMultiPoint
     */
    @JvmStatic
    fun toGeometryMultiPoint(
        value: String
    ): ConvertResult<GeometryMultiPoint> {
        return try {
            ConvertResult.ConvertSuccess(GeometryMultiPoint.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryLineString
     */
    @JvmStatic
    fun toGeometryLineString(
        value: String
    ): ConvertResult<GeometryLineString> {
        return try {
            ConvertResult.ConvertSuccess(GeometryLineString.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryMultiLineString
     */
    @JvmStatic
    fun toGeometryMultiLineString(
        value: String
    ): ConvertResult<GeometryMultiLineString> {
        return try {
            ConvertResult.ConvertSuccess(GeometryMultiLineString.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryPolygon
     */
    @JvmStatic
    fun toGeometryPolygon(
        value: String
    ): ConvertResult<GeometryPolygon> {
        return try {
            ConvertResult.ConvertSuccess(GeometryPolygon.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }

    /*
     * For OData types: Edm.GeometryMultiPolygon
     */
    @JvmStatic
    fun toGeometryMultiPolygon(
        value: String
    ): ConvertResult<GeometryMultiPolygon> {
        return try {
            ConvertResult.ConvertSuccess(GeometryMultiPolygon.parseWKT(value))
        } catch (e: Exception) {
            ConvertResult.ConvertError(e)
        }
    }
}
