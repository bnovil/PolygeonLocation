package polygon.position;

import polygon.common.HttpUtil;
import polygon.common.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lzq on 2017/4/13.
 */
public class Polygon {
    double lat = 0;
    double lon = 0;

    public String polygonPosition(String[][] positionweight){
        if(positionweight==null){
            System.out.println("无基站数据");
            return null;
        } else {
            double[][] latlonweight = new double[positionweight.length][3];
            for(int i=0;i<positionweight.length;i++){
                for(int j=0;j<positionweight[0].length;j++){
                    latlonweight[i][j] = Double.parseDouble(positionweight[i][j]);
                }
            }

            //权重
            double w = 0;
            for (int i=0;i<latlonweight.length;i++){
                w = w + latlonweight[i][2];
            }

            for (int i=0;i<latlonweight.length;i++) {
                lat = lat+latlonweight[i][0]*latlonweight[i][2];
                lon = lon+latlonweight[i][1]*latlonweight[i][2];
            }
            lat = lat/w;
            lon = lon/w;
            String latstr = lat + "";
            String lonstr = lon + "";
            return lonstr.substring(0,10)+","+latstr.substring(0,9);
        }

    }

    public double getDistance(double rssi){
        double n = 2.5;
        double p0 = -70;
		//基站到定位终端的距离
        double rawDistance;
        rawDistance =Math.pow(10, Math.abs((p0-rssi))/(10*n));
        return rawDistance;
    }

    public String[][] btslocation(String bts){
        String[] btslist = bts.split("\\|");
        for(int i=0;i<btslist.length;i++){
            System.out.println(btslist[i]);
        }
        String[][] latlonresult = new String[btslist.length][3];
        /**调用Cellocation 基站查询接口
         *请求示例 http://api.cellocation.com/cell/?mcc=460&mnc=1&lac=9365&ci=4643&output=json
         */
        String url = "http://api.cellocation.com/cell/";
        String result = null;
        for(int i=0;i<btslist.length;i++) {
            String[] btstemp = btslist[i].split(",");
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("mcc", btstemp[0]);
            params.put("mnc", btstemp[1]);
            params.put("lac", btstemp[2]);
            params.put("ci", btstemp[3]);
            params.put("output", "json");

            result = HttpUtil.httpGet(url, params);
            if(result==null){
                System.out.println("调用接口失败");
                return null;
            }
            else{
                Map Jsonresult = JsonUtil.jsonToMap(result);
                latlonresult[i][0] = (String) Jsonresult.get("lat");
                latlonresult[i][1] = (String) Jsonresult.get("lon");
                //根据信号强度计算距离，以距离的倒数作为权重
                double d = getDistance(Double.parseDouble(btstemp[4]));
                latlonresult[i][2] = 1/d+"";
            }
        }
        return latlonresult;
    }

    public String getPositon(String bts){
        String[][] r = btslocation(bts);
        for (int i = 0; i < r.length; i++) {
            System.out.println("各基站位置："+r[i][1]+","+r[i][0]);
        }

        String p = polygonPosition(r);
        return p;
    }
    //计算误差
    public String calDeviation (String s1, String s2){

        return null;
    }

    public static void main(String[] arg) {
        Polygon polygon = new Polygon();
        String test = "460,0,9365,4643,-77|460,0,9365,4213,-80|460,0,9365,4212,-81|460,0,9763,4391,-85|460,0,9365,5281,-86|460,0,9365,3602,-87";
        String p = polygon.getPositon(test);
        System.out.println("终端位置："+p);
    }

}


