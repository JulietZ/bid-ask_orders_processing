package financial;

/**
 * Created by Juliet on 3/9/14.
 */
//import jz1353.financial.Message;

public interface NewOrder extends Message{



    /**
     * @return The symbol for this new order.
     */
    public String getSymbol();

    /**
     * @return The size of the order. Negative for sell
     */
    public int getSize();

    /**
     * @return The orderId for this new order
     */
    public String getOrderId();

    /**
     * @return The limit price for the order. NaN for market Order.
     */
    public double getLimitPrice();
}
