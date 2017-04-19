package polygon.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * json tools;
 *
 * @author yuTong
 * @since 2015/11/07 15:28:47
 */
public class JsonUtil {

    private volatile static Gson gson;
    private volatile static ObjectMapper objectMapper; //jackson

    static {
        if (null == gson) {
            synchronized (JsonUtil.class) {
                if (null == gson) {
                    gson = new Gson();
                }
            }
        }
        if (null == objectMapper) {
            synchronized (JsonUtil.class) {
                if (null == objectMapper) {
                    objectMapper = new ObjectMapper();
                    objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);//空值不序列化
                    //反序列化时，属性不存在的兼容处理
                    objectMapper.getDeserializationConfig()
                            .withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                }
            }
        }
    }

    private JsonUtil() {
    }

    /**
     * 将对象转换成json格式
     *
     * @param ts
     * @return
     */
    public static String objectToJson(Object ts) {
        String jsonStr = null;
        if (gson != null) {
            jsonStr = gson.toJson(ts);
        }
        return jsonStr;
    }

    /**
     * 将对象转换成json格式(并自定义日期格式)
     *
     * @param ts
     * @return
     */
    public static String objectToJsonDateSerializer(Object ts, final String dateFormat) {
        gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class, (JsonSerializer<Date>) (src, typeOfSrc, context) -> {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            return new JsonPrimitive(format.format(src));
        }).setDateFormat(dateFormat).create();
        return gson.toJson(ts);
    }

    /**
     * 将json格式转换成list对象
     *
     * @param jsonStr
     * @return
     */
    public static List jsonToList(String jsonStr) {
        List objList = null;
        if (gson != null) {
            Type type = new TypeToken<List<?>>() {
            }.getType();
            objList = gson.fromJson(jsonStr, type);
        }
        return objList;
    }

    /**
     * @param jsonStr
     * @param type
     * @return List<T>
     * @Title: jsonToList
     * @Description: 将Json转为对应的List
     */
    public static <T> List<T> jsonToList(String jsonStr, Class<T> type) {
        Type listType = new TypeToken<ArrayList<T>>() {
        }.getType();
        List<T> list = null;
        if (gson != null) {
            list = gson.fromJson(jsonStr, listType);
        }
        return list;
    }

    /**
     * 将json格式转换成map对象
     *
     * @param jsonStr
     * @return
     */
    public static <K, V> Map<K, V> jsonToMap(String jsonStr) {
        Map<K, V> objMap = null;
        if (gson != null) {
            Type type = new TypeToken<Map<K, V>>() {
            }.getType();
            objMap = gson.fromJson(jsonStr, type);
        }
        return objMap;
    }

    /**
     * 将json转换成bean对象
     *
     * @param jsonStr
     * @return
     */
    public static <T> T jsonToBean(String jsonStr, Class<T> cl) {
        return gson != null ? gson.fromJson(jsonStr, cl) : null;
    }

    /**
     * 将json转换成bean对象
     *
     * @param jsonStr
     * @param cl
     * @return
     */
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl, final String pattern) {
        T bean;
        gson = new GsonBuilder().registerTypeAdapter(Date.class, (JsonDeserializer<Date>) (json, typeOfT, context) -> {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            String dateStr = json.getAsString();
            try {
                return format.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }).setDateFormat(pattern).create();
        bean = gson.fromJson(jsonStr, cl);
        return bean;
    }

    /***
     * 获取json键的值
     * @param jsonStr
     * @param k
     * @return
     */
    public static <K, V> V getJsonValue(String jsonStr, K k) {
        V bean = null;
        Map<K, V> resultMap = jsonToMap(jsonStr);
        if (CollectionUtils.isEmpty(resultMap)) {
            bean = resultMap.get(k);
        }
        return bean;
    }

    /***
     * json 转化为Bean jackson2 框架
     * @param json
     * @param tClass {@link Class}
     * @param pattern {@link DateFormat}
     * @param <T>
     * @return T
     * @throws IOException
     */
    public static <T> T jsonToBean(String json, Class<T> tClass, String pattern) throws IOException {
        objectMapper.setDateFormat(new SimpleDateFormat(pattern));
        return objectMapper.readValue(json, tClass);
    }

    /***
     * object 转化为json jackson2 框架
     * @param object {@link Object}
     * @param pattern {@link DateFormat}
     * @return String
     * @throws JsonProcessingException
     */
    public static String objectToJson(Object object, String pattern) throws JsonProcessingException {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        objectMapper.setDateFormat(dateFormat);
        return objectMapper.writeValueAsString(object);
    }
}