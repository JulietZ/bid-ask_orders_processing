package financial; /**
 * Created by Juliet on 3/9/14.
 */

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class OrdersIterator {

    static  class NewOrderImpl implements NewOrder{
        private String symbol;
        private String orderId;
        public int size;
        public double limitPrice;

        NewOrderImpl(String symbol, String orderId, int size, double limitPrice) {
            this.symbol = symbol;
            this.orderId = orderId;
            this.size = size;
            this.limitPrice = limitPrice;
        }



        public String getSymbol() {
            return symbol;
        }

        public String getOrderId() {
            return orderId;
        }

        public int getSize() {
            return size;
        }

        public double getLimitPrice() {
            return limitPrice;
        }
    }

    static class OrderCxRImpl implements OrderCxR{

        private int size;
        private double limitPx;
        private String orderId;


        public OrderCxRImpl(int size, double limitPx, String orderId) {
            this.size = size;
            this.limitPx = limitPx;
            this.orderId = orderId;
        }

        public int getSize() {
            return size;
        }

        public double getLimitPrice() {
            return limitPx;
        }

        public String getOrderId() {
            return orderId;
        }
    }

    private List<Message> msgs;

    public OrdersIterator(){
        msgs = new LinkedList<Message>();
        msgs.add(new NewOrderImpl("IBM","ABC1",1000,100.00));
        msgs.add(new NewOrderImpl("IBM","IBM1",1000,100.00));
        msgs.add(new NewOrderImpl("IBM","IBM2",1000,99.00));
        msgs.add(new NewOrderImpl("IBM","IBM3",1000,98.00));
        msgs.add(new NewOrderImpl("IBM","IBM4",-3500,99.00));
        //msgs.add(new OrderCxRImpl(1200, 101.01, "IBM3"));
        //msgs.add(new OrderCxRImpl(1200, 101.01, "IBM4"));
        msgs.add(new NewOrderImpl("IBM","IBM3", 100, Double.NaN));
    }


    public Iterator<Message> getIterator(){
        return msgs.iterator();

    }
}
