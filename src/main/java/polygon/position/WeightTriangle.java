package polygon.position;

/**
 * Created by lzq on 2017/4/13.
 */
public class WeightTriangle {

    public static void main(String[] arg){
        Polygon triangle = new Polygon();
        String test = "460,0,9365,4213,-80|460,0,9365,4212,-81|460,0,9763,4391,-85";
//        String[][] r = triangle.btslocation(test);
//        for(int i=0;i<r.length;i++){
//            for(int j=0;j<r[0].length;j++){
//                System.out.println(r[i][j]);
//            }
//        }
//        String p = triangle.polygonPosition(r);
//        System.out.println(p);
        String[] a = {"a","b","c","d","e"};
        CombineAlgorithm ca = null;
        try {
            ca = new CombineAlgorithm(a,3);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Object[][] c = ca.getResult();

    }
}
