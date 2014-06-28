package financial;

/**
 * Created by Juliet on 3/11/14.
 */


import financial.OrdersIterator.NewOrderImpl;
import financial.OrdersIterator.OrderCxRImpl;

import java.util.Iterator;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashMap;
public class book {

    // There are two HashMap, which names bid and ask. Their key is a String value, which is the symbol of the orders, like "IBM","MSFT",etc. Their value is the book which is a hashtable to store the orders of that symbol.
    // So like the bid book of IBM can be get by bid.get("IBM")
    // In the hashtable，the key is the price of all the order, which is a Double, while the value is a linkedlist stored all the orders which price is the key.
    // The bid which store all the bid orders, and the ask stores all the ask orders.

    static HashMap<String,Hashtable<Double,LinkedList<NewOrderImpl>>> bid=new HashMap<String,Hashtable<Double,LinkedList<NewOrderImpl>>> ();
    static HashMap<String,Hashtable<Double,LinkedList<NewOrderImpl>>> ask=new HashMap<String,Hashtable<Double,LinkedList<NewOrderImpl>>> ();

    //build a new iterator.
    OrdersIterator ordersIterator=new OrdersIterator();

    /**
     * This method is mainly to get the best bid price of a certain bidbook, which also means the best bid price of the certain symbol.
     * the method traverses all over the bidbook, to get the largest key with value in bidbook, which is the best bid price.
     * and it will return Double.MIN_VALUE, if there is no orders in bidbook right now.
     */
    public Double getHighestBid (Hashtable<Double,LinkedList<NewOrderImpl>> bidbook){
        //build a set naming keys, which contains all the keys in bidbook;
        Set<Double> keys = bidbook.keySet();
        //initialize the best bid price with Double.MIN_VALUE, which i called highestBid here.
        Double highestBid=Double.MIN_VALUE;
        //traverse all the key in the set keys, to get the largest one in the set.
        for(Double key: keys){
            if(bidbook.get(key).size() > 0 && key > highestBid){
                highestBid =key;
            }
        }
        //Finally, return the result, highestBid;
        return highestBid;
    }

    /**
     * This method is mainly to get the best ask price of a certain askbook, which also means the best ask price of the certain symbol.
     * the method traverses all over the askbook, to get the smallest key with value in askbook, which is the best ask price.
     * and it will return Double.MAX_VALUE, if there is no orders in askbook right now.
     */
    public Double getLowestAsk (Hashtable<Double,LinkedList<NewOrderImpl>> askbook){
        //build a set naming keys, which contains all the keys in askbook;
        Set<Double> keys = askbook.keySet();
        //initialize the best ask price with Double.MAX_VALUE, which i called lowestAsk here.
        Double lowestAsk=Double.MAX_VALUE;
        //traverse all the key in the set keys, to get the smallest one in the set.
        for(Double key: keys){
            if(askbook.get(key).size() > 0 && key < lowestAsk){
                lowestAsk =key;
            }
        }
        //Finally, return the result, lowestAsk;
        return lowestAsk;
    }

    /**
     * This method is mainly to add a new order into the certain bidbook or askbook , which named @param myNewOrder
     * the method returns nothing, but has side effect of changing that bidbook or askbook.
     * Once get a new order, first check it is a bid order or an ask order. If the size of the order is positive, then it is a bid order, otherwise, it is a ask order.
     * Then you judge the price of the order. If the price shows Double.NaN,it means this is a marketprice order, in another words, any price is okay for this order to make a deal.
     * After that i make a deal if the situation is allowed. If there is no trade can be done, then push the order into the according financial.book.
     */
    public void addNewOrder (NewOrderImpl myNewOrder,String symbol,Hashtable<Double,LinkedList<NewOrderImpl>> bidbook, Hashtable<Double,LinkedList<NewOrderImpl>> askbook) {
        //myprice is a double, which is the limitprice of the input myNewOrder.
        double myprice=myNewOrder.getLimitPrice();
        //temp is a linkedlist with the object NewOrderImpl, which is intended to be used as the list contains the orders which the new order could be traded with.
        LinkedList<NewOrderImpl> temp = new LinkedList<NewOrderImpl>();
        //to judge if myNewOrder is a bid order.
        if(myNewOrder.size >= 0){
            //to judge if myNewOrder is with marketprice.
            if (myprice == Double.NaN){

                //get the best ask price, named marketprice.
                Double marketprice=getLowestAsk(askbook);
                //get the ask orders in askbook which price the marketprice, also meaning these orders have the priority to trade with the new order.
                LinkedList<NewOrderImpl> asktrade= askbook.get(marketprice);
                /**
                 * The trade part
                 * As long as the new order with markerprice hasn't been traded all, we find the ask orders with current best ask price to share its size.
                 * Like been written below, if the size of the order we get first is smaller than the remaining size of the new order, then done the trade, and decrease the size of the new order, and then find the next best ask order to trade.
                 * if the size of the best ask order is larger than the remaining size of the new order, then done the trade, and cut the share of the best ask order.
                 * If the orders in asktrade is all be traded, but there is still some shares in new order, we get the next best ask price, and get a new list of orders with that price.
                 * We recursively do the steps above, until the new order is traded completely.
                 */
                while(myNewOrder.size!=0){
                    if(asktrade!=null){
                        NewOrderImpl firstOrder= asktrade.getFirst();
                        if(Math.abs(firstOrder.size)> Math.abs(myNewOrder.size)){
                            firstOrder.size+=myNewOrder.size;
                            myNewOrder.size=0;
                        }
                        else {
                            myNewOrder.size+=firstOrder.size;
                            asktrade.removeFirst();
                        }
                    }
                    else {
                        marketprice=getHighestBid(bidbook);
                        asktrade= bidbook.get(marketprice);
                    }
                }
            }
            //if myNewOrder is with limitprice
            else {
                // get the current best ask price, which named marketprice
                Double marketprice=getLowestAsk(askbook);
                // to check if there is some ask orders can be traded with the new order right now, which means the best ask price is smaller than the limit price of new order.
                if(marketprice <= myprice){
                    // build a new linked list to contain the orders with the best price to be traded with the new order.
                    LinkedList<NewOrderImpl> asktrade=new LinkedList<NewOrderImpl>();
                    /**
                     * The trade part
                     * As long as the new order with markerprice hasn't been traded all, we find the ask orders with current best ask price to share its size.
                     * Like been written below, if the size of the order we get first is smaller than the remaining size of the new order, then done the trade, and decrease the size of the new order, and then find the next best ask order to trade.
                     * if the size of the best ask order is larger than the remaining size of the new order, then done the trade, and cut the share of the best ask order.
                     * then we get the latest best ask price.
                     * We recursively do the steps above, unless the new order's size come to zero or there is no ask order can be traded with the new order.
                     */
                    while (myNewOrder.size!=0&&marketprice<=myprice){
                        asktrade = askbook.get(marketprice);
                        NewOrderImpl firstOrder= asktrade.getFirst();
                        if(Math.abs(firstOrder.size)>Math.abs(myNewOrder.size)){
                            firstOrder.size+=myNewOrder.size;
                            myNewOrder.size=0;
                        }
                        else {
                            myNewOrder.size+=firstOrder.size;
                            asktrade.removeFirst();
                        }
                        marketprice=getLowestAsk(askbook);
                    }
                    //if after the trade above, the new order is still not empty, then put it into the bid financial.book.
                    if (myNewOrder.size!=0){
                        if (bidbook.containsKey(myprice)){
                            temp=bidbook.get(myprice);
                        }
                        temp.push(myNewOrder);
                        bidbook.put(myprice,temp);
                    }
                }
                //if there is no order can be traded with the new order, just put it into the bid financial.book.
                else {
                    if (bidbook.containsKey(myprice)){
                        temp=bidbook.get(myprice);
                    }
                    temp.push(myNewOrder);
                    bidbook.put(myprice,temp);
                }
            }
        }
        //if the new order is a ask order.
        else {
            //to judge if myNewOrder is with marketprice.
            if (myprice == Double.NaN){

                //get the best bid price, named marketprice.
                Double marketprice=getHighestBid(bidbook);
                //get the bid orders in bidbook which price the marketprice, also meaning these orders have the priority to trade with the new order.
                LinkedList<NewOrderImpl> bidtrade= bidbook.get(marketprice);
                /**
                 * The trade part
                 * As long as the new order with markerprice hasn't been traded all, we find the bid orders with current best bid price to share its size.
                 * Like been written below, if the size of the order we get first is smaller than the remaining size of the new order, then done the trade, and decrease the size of the new order.
                 * if the size of the best bid order is larger than the remaining size of the new order, then done the trade, and cut the share of the best bid order.
                 * If the orders in bidtrade is all be traded, but there is still some shares in new order, we get the next best bid price, and get a new list of orders with that price.
                 * We recursively do the steps above, until the new order is traded completely.
                 */
                while(myNewOrder.size!=0){
                    if(bidtrade!=null){
                        NewOrderImpl firstOrder= bidtrade.getFirst();
                        if(Math.abs(firstOrder.size)> Math.abs(myNewOrder.size)){
                            firstOrder.size+=myNewOrder.size;
                            myNewOrder.size=0;
                        }
                        else {
                            myNewOrder.size+=firstOrder.size;
                            bidtrade.removeFirst();
                        }
                    }
                    else {
                        marketprice=getHighestBid(bidbook);
                        bidtrade= bidbook.get(marketprice);
                    }
                }
            }
            //if myNewOrder is with limitprice
            else {
                // get the current best bid price, which named marketprice
                Double marketprice=getHighestBid(bidbook);
                // to check if there is some bid orders can be traded with the new order right now, which means the best bid price is larger than the limit price of new order.
                if(marketprice >= myprice){
                    // build a new linked list to contain the orders with the best price to be traded with the new order.
                    LinkedList<NewOrderImpl> bidtrade=new LinkedList<NewOrderImpl>();
                    /**
                     * The trade part
                     * As long as the new order with markerprice hasn't been traded all, we find the bid orders with current best bid price to share its size.
                     * Like been written below, if the size of the order we get first is smaller than the remaining size of the new order, then done the trade, and decrease the size of the new order.
                     * if the size of the best bid order is larger than the remaining size of the new order, then done the trade, and cut the share of the best bid order.
                     * then we get the latest best bid price.
                     * We recursively do the steps above, unless the new order's size come to zero or there is no bid order can be traded with the new order.
                     */
                    while (myNewOrder.size!=0&&marketprice>=myprice){
                        bidtrade = bidbook.get(marketprice);
                        NewOrderImpl firstOrder= bidtrade.getFirst();
                        if(Math.abs(firstOrder.size)>Math.abs(myNewOrder.size)){
                            firstOrder.size+=myNewOrder.size;
                            myNewOrder.size=0;
                        }
                        else {
                            myNewOrder.size+=firstOrder.size;
                            bidtrade.removeFirst();
                        }
                        marketprice=getHighestBid(bidbook);
                    }
                    //if after the trade above, the new order is still not empty, then put it into the ask financial.book.
                    if (myNewOrder.size!=0){
                        if (askbook.containsKey(myprice)){
                            temp=askbook.get(myprice);
                        }
                        temp.push(myNewOrder);
                        askbook.put(myprice, temp);
                    }
                }
                //if there is no order can be traded with the new order, just put it into the ask financial.book.
                else {
                    if (askbook.containsKey(myprice)){
                        temp=askbook.get(myprice);
                    }
                    temp.push(myNewOrder);
                    askbook.put(myprice, temp);
                }
            }
        }
        bid.put(symbol,bidbook);
        ask.put(symbol,askbook);
    }

    /**
     * This method is mainly to look up an order which is already in one bidbook or askbook, according to its unique id.
     * For I do not know which book is this order in, one certain symbol's bidbook or askbook. So I need to go though all the bid book in bid and all the ask book in ask to find it.
     * I choose first to look up in all the bidbook, if there is nothing return, then look up in all the askbook.
     * What I actually do is to look up every key in the book, and traverse the linkedlist with that key. When I go through the linkedlist with all the order, i match their id
     * with the id I am looking for. As soon as i find the matching one, i remove the order from the financial.book, and return the order i found.
     */
    public NewOrderImpl lookUpOrder (String id){
        //get all the key in bid
        Set<String> keys=bid.keySet();
        Hashtable<Double,LinkedList<NewOrderImpl>> temptable=new Hashtable<Double, LinkedList<NewOrderImpl>>();
        //go through all the key in bid
        for(String key:keys){
            //get the book of that symbol.
            temptable=bid.get(key);
            //the temp linkedlist is to store the linkedlist i will be traverse.
            LinkedList<NewOrderImpl> temp;
            //the replaceOrder is the order which matches the id and to be returned.
            //the tmp is the order which used to get from the linkedlist to compare with the id i need.
            NewOrderImpl replaceOrder,tmp;
            //get all the keys in bidbook, so i can traverse all of it.
            Set<Double> tablekey = temptable.keySet();
            //I traverse all the key, and put the value to the key into the linkedlist temp.
            for(Double key2: tablekey){
                temp=temptable.get(key2);
                //i build an iterator to traverse the linkedlist temp
                for(Iterator<NewOrderImpl> it = temp.iterator(); it.hasNext();) {
                    tmp=it.next();
                    //to compare all the orders in temp, to see if there is an order matches the id.
                    if(tmp.getOrderId().equals(id)){
                        //found one
                        replaceOrder=tmp;
                        //remove it from the financial.book
                        temp.remove(tmp);
                        temptable.put(key2,temp);
                        bid.put(key,temptable);
                        //return the result.
                        return replaceOrder;
                    }
                }
            }
        }
        //get all the key in ask
        keys=ask.keySet();
        //go through all the key in ask
        for(String key:keys){
            //get the askbook of certain symbol
            temptable=ask.get(key);
            //the temp linkedlist is to store the linkedlist i will be traverse.
            LinkedList<NewOrderImpl> temp;
            //the replaceOrder is the order which matches the id and to be returned.
            //the tmp is the order which used to get from the linkedlist to compare with the id i need.
            NewOrderImpl replaceOrder,tmp;
            //get all the keys in askbook, so i can traverse all of it.
            Set<Double> tablekey = temptable.keySet();
            //I traverse all the key, and put the value to the key into the linkedlist temp.
            for(Double key2: tablekey){
                temp=temptable.get(key2);
                //i build an iterator to traverse the linkedlist temp
                for(Iterator<NewOrderImpl> it = temp.iterator(); it.hasNext();) {
                    tmp=it.next();
                    //to compare all the orders in temp, to see if there is an order matches the id.
                    if(tmp.getOrderId().equals(id)){
                        //found one
                        replaceOrder=tmp;
                        //remove it from the financial.book
                        temp.remove(tmp);
                        temptable.put(key2,temp);
                        ask.put(key, temptable);
                        //return the result.
                        return replaceOrder;
                    }
                }
            }
        }
        //did not find the order neither in bid financial.book nor in ask financial.book, so return null.
        return null;
    }

    /**
     * This method is mainly to cancel replace an order.
     * We find the original order in the books, then we renew its information.
     * then we push it back to the financial.book as a new order.
     */
    public String cxrOrder (OrderCxRImpl changeOrder){
        //get the id of the cancel replace order
        String orderID= changeOrder.getOrderId();
        //find the order in the financial.book
        NewOrderImpl renewOrder = lookUpOrder(orderID);
        //renew the order, then push it back into the financial.book as a new order
        String orderSymbol= renewOrder.getSymbol();
        if (renewOrder!=null&&changeOrder.getSize()!=0){
            //renew the size of the order
            renewOrder.size=changeOrder.getSize();
            //renew the price of the order
            renewOrder.limitPrice= changeOrder.getLimitPrice();
            //get the bidbook and askbook of that certain symbol
            Hashtable<Double,LinkedList<NewOrderImpl>> tempbid= new Hashtable<Double,LinkedList<NewOrderImpl>>();
            Hashtable<Double,LinkedList<NewOrderImpl>> tempask= new Hashtable<Double,LinkedList<NewOrderImpl>>();
            if (bid.containsKey(orderSymbol)){
                tempbid=bid.get(orderSymbol);
            }
            if (ask.containsKey(orderSymbol)){
                tempask=ask.get(orderSymbol);
            }
            //add the changed order back to the books.
            addNewOrder (renewOrder,orderSymbol,tempbid,tempask);
        }
        //return the symbol of the cancel and replace order.
        return orderSymbol;
    }

    /**
     *This result method is to get all things done by using the method build above.
     *It get all the orders and deal with them.
     *Every time right after one oder has been solved, it will print out the best bid price and ask price for the name that the message belonged to.
     */
    public static void result(Iterator<Message> myiter, Boolean flag){
        //get a new book
        book book = new book();
        //this recursion will not end until the mylist is empty.
        while (myiter.hasNext()){
            //if the next item in mylist is a new Order.
            Object mymessage= myiter.next();
            //be prepared to get the certain bidbook and askbook
            Hashtable<Double,LinkedList<NewOrderImpl>> tempbid= new Hashtable<Double,LinkedList<NewOrderImpl>>();
            Hashtable<Double,LinkedList<NewOrderImpl>> tempask= new Hashtable<Double,LinkedList<NewOrderImpl>>();
            //if the next item is a new order
            if(mymessage instanceof NewOrderImpl) {
                //make the order a NewOrderImpl
                NewOrderImpl myneworder= (NewOrderImpl) mymessage;
                //get the symbol of the order
                String mysymbol=myneworder.getSymbol();
                //if the bidbook of the symbol is already built, fetch the bidbook
                if (bid.containsKey(mysymbol)){
                    tempbid=bid.get(mysymbol);
                }
                //if the askbook of the symbol is already build, fetch the askbook
                if (ask.containsKey(mysymbol)){
                    tempask=ask.get(mysymbol);
                }
                //add the new order into the books.
                book.addNewOrder(myneworder, mysymbol, tempbid, tempask);
                //if the flag is true, print out the information
                if (flag){
                    //if there is no bid orders in that bidbook, return Double.MIN_VALUE, and print out that there is no bid orders.
                    if(book.getHighestBid(tempbid)==Double.MIN_VALUE) System.out.println(mysymbol+" Best Bid Price:  no bid orders");
                    //else print out the best bid price
                    else System.out.println(mysymbol+" Best Bid Price:  " + book.getHighestBid(tempbid));
                    //if there is no ask orders in that askbook, return Double.MAX_VALUE, and print out that there is no ask orders.
                    if(book.getLowestAsk(tempask)==Double.MAX_VALUE) System.out.println(mysymbol+" Best Ask Price:  no ask orders");
                    //else print out the best ask price
                    else System.out.println(mysymbol+" Best Ask Price:  " + book.getLowestAsk(tempask));
                }
            }
            //if the next item in mylist is a Order CxR.
            else if (mymessage instanceof OrderCxRImpl){
                //make the item a OrderCxRImpl, then use the cxrOrder method to deal the order, and get its symbol
                String mysymbol= book.cxrOrder((OrderCxRImpl) mymessage);
                //fetch the bidbook and askbook of that symbol
                tempbid=bid.get(mysymbol);
                tempask=ask.get(mysymbol);
                //if the flag is true, print out the information
                if (flag){
                    //if there is no bid orders in that bidbook, return Double.MIN_VALUE, and print out that there is no bid orders.
                    if(book.getHighestBid(tempbid)==Double.MIN_VALUE) System.out.println(mysymbol+" Best Bid Price:  no bid orders");
                        //else print out the best bid price
                    else System.out.println(mysymbol+" Best Bid Price:  " + book.getHighestBid(tempbid));
                    //if there is no ask orders in that askbook, return Double.MAX_VALUE, and print out that there is no ask orders.
                    if(book.getLowestAsk(tempask)==Double.MAX_VALUE) System.out.println(mysymbol+" Best Ask Price:  no ask orders");
                        //else print out the best ask price
                    else System.out.println(mysymbol+" Best Ask Price:  " + book.getLowestAsk(tempask));
                }
            }
        }
    }

    /**
     *This is the main method
     *It build a new book, and test the program
     */
    public static void main(String[] args){
        book test =new book();
        test.result(test.ordersIterator.getIterator(),true);

    }
}
