package com.parulenterprise.db;

import com.parulenterprise.model.Item;
import com.parulenterprise.model.Product;

import java.sql.*;
import java.util.*;

public class DataSource {
    private static final String DB_NAME = "stock.db";
    private static final String CONNECTION_STRING ="jdbc:sqlite:" + System.getProperty("user.dir") + "\\" + DB_NAME;

    //SQLite connection statement
    private Connection connection;

    //static Strings for table: Products
    private static final String TABLE_PRODUCTS = "products";
    private static final String COLUMN_PRODUCT_ID = "_id";
    private static final String COLUMN_PRODUCT_NAME = "name";

    //static Strings for table: Items
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ITEM_ID = "_id";
    private static final String COLUMN_ITEM_NAME = "name";
    private static final String COLUMN_ITEM_PAGES = "pages";
    private static final String COLUMN_ITEM_PACKAGE = "pkg";
    private static final String COLUMN_ITEM_PRICE = "price";
    private static final String COLUMN_ITEM_STOCK = "stock";
    private static final String COLUMN_ITEM_PRODUCT = "product";

    //static Strings for table: SubItems
    private static final String TABLE_SUBITEMS = "subItems";
    private static final String COLUMN_SUBITEM_ID = "_id";
    private static final String COLUMN_SUBITEM_NAME = "name";
    private static final String COLUMN_SUBITEM_ITEM = "item";

    //query strings
    private static final String QUERY_ALL_ITEMS = "SELECT * FROM "+ TABLE_ITEMS;
    private static final String QUERY_ALL_PRODUCTS = "SELECT * FROM " + TABLE_PRODUCTS;
    private static final String UPDATE_ITEM_STOCK = "UPDATE "+ TABLE_ITEMS +" SET "+ COLUMN_ITEM_STOCK +" = ? WHERE "
            + COLUMN_ITEM_ID +" = ?";
    private static final String EDIT_ITEM = "UPDATE "+ TABLE_ITEMS +" SET "+ COLUMN_ITEM_NAME+" = ?, "+
            COLUMN_ITEM_PAGES+" = ?, "+ COLUMN_ITEM_PACKAGE +" = ?, "+
            COLUMN_ITEM_STOCK+" = ?, "+ COLUMN_ITEM_PRICE +" = ? " +
            "WHERE "+ COLUMN_ITEM_ID +" = ?";
    private static final String DELETE_ITEM = "DELETE FROM "+ TABLE_ITEMS +" WHERE "+ COLUMN_ITEM_ID+" = ?";
    private static final String ADD_ITEM = "INSERT INTO "+ TABLE_ITEMS +" ("+ COLUMN_ITEM_ID+", "
            + COLUMN_ITEM_NAME +", "+ COLUMN_ITEM_PAGES +", "+ COLUMN_ITEM_PACKAGE +", "
            + COLUMN_ITEM_PRICE +", "+ COLUMN_ITEM_STOCK +", "+ COLUMN_ITEM_PRODUCT +") " +
            "VALUES ( ?, ?, ?, ?, ?, ?, ?)";

    //Prepared Statements
    private PreparedStatement queryAllItems;
    private PreparedStatement queryAllProducts;
    private PreparedStatement updateItemStock;
    private PreparedStatement editItem;
    private PreparedStatement deleteItem;
    private PreparedStatement addItem;

    //Singleton class
    private static DataSource dataSource = new DataSource();

    private Map<String, Integer> productStock;
    private List<Product> products;
    private List<Item> items;

    private DataSource(){

    }

    public static DataSource getInstance(){
        return dataSource;
    }

    // open/access database resource
    public boolean open(){
        try {
            System.out.println(CONNECTION_STRING);
            connection = DriverManager.getConnection(CONNECTION_STRING);
            updateItemStock = connection.prepareStatement(UPDATE_ITEM_STOCK);
            editItem = connection.prepareStatement(EDIT_ITEM);
            deleteItem = connection.prepareStatement(DELETE_ITEM);
            addItem = connection.prepareStatement(ADD_ITEM);

            initialize();
            return true;
        } catch (SQLException e) {
            System.out.println("Connection open error: " + e.getMessage());
            return false;
        }
    }

    //close database resource
    public boolean close(){
        try{
            if(addItem != null){
                addItem.close();
            }
            if(deleteItem != null){
                deleteItem.close();
            }
            if(editItem != null){
                editItem.close();
            }
            if(updateItemStock != null){
                updateItemStock.close();
            }
            if(queryAllItems != null) {
                queryAllItems.close();
            }
            if(queryAllProducts != null){
                queryAllProducts.close();
            }
            connection.close();

            return true;
        } catch (SQLException e) {
            System.out.println("Connection close error: " + e.getMessage());
            return false;
        }
    }

    private void initialize(){
        queryItem();
        queryProducts();
    }

    public Map<String, Integer> getProductStock() {
        return productStock;
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Item> getItems() {
        return items;
    }

    public boolean addItem(Item item){
        //(1, _id) (2, name) (3, pages) (4, package) (5, price) (6, stock) (7, productId)
        try(PreparedStatement statement = connection.prepareStatement(ADD_ITEM)){
            statement.setString(1, item.getId());
            statement.setString(2, item.getName());
            statement.setInt(3, item.getPages());
            statement.setInt(4, item.getPackageSize());
            statement.setDouble(5, item.getPrice());
            statement.setInt(6, item.getStock());
            statement.setString(7, item.getProductID());

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding item");
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(Item item){
        //(1, _id)
        try(PreparedStatement statement = connection.prepareStatement(DELETE_ITEM)){
            statement.setString(1, item.getId());

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error deleting item");
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItemStock(Item item){
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ITEM_STOCK)){
            statement.setInt(1, item.getStock());
            statement.setString(2, item.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error adding stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean editItem(Item item){
        //(1, name) (2, pages) (3, package) (4, stock) (5, price) (6, _id)
        try(PreparedStatement statement = connection.prepareStatement(EDIT_ITEM)){
            statement.setString(1, item.getName());
            statement.setInt(2, item.getPages());
            statement.setInt(3, item.getPackageSize());
            statement.setInt(4, item.getStock());
            statement.setDouble(5, item.getPrice());
            statement.setString(6, item.getId());

            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error updating data");
            e.printStackTrace();
            return false;
        }
    }

    //Method is called in initialize()
    private List<Product> queryProducts(){
        try{
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(QUERY_ALL_PRODUCTS);

            products = new LinkedList<>();
            while(results.next()){
                Product product = new Product();
                product.setId(results.getString(COLUMN_PRODUCT_ID));
                product.setName(results.getString(COLUMN_PRODUCT_NAME));
                product.setStock(productStock.get(product.getId()));

                products.add(product);
            }

            return products;
        } catch (SQLException e) {
            System.out.println("Query Products error: " + e.getMessage());
            return null;
        }
    }

    //This is the public method required to query all items from Items table in stock.db
    //This method uses queryAllItem, private method to get data from the database
    //also this class will collect total stock per product
    private List<Item> queryItem(){
        items = queryAllItems();

        productStock = new LinkedHashMap<>();
        if(items != null) {
            for (Item item : items) {
                if (productStock != null) {
                    //if its the first entry for the current product
                    if (productStock.containsKey(item.getProductID())) {
                        int total = productStock.get(item.getProductID()) + item.getStock();
                        productStock.put(item.getProductID(), total);
                    } else {
                        productStock.put(item.getProductID(), item.getStock());
                    }
                }
            }
        }
        return items;
    }

    //This class is private due to additional processing required to collect total stock of each product
    //by this we will need to query the table Items from database just once
    //Look for queryItem() for public equivalent method
    private List<Item> queryAllItems(){
        try{
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery(QUERY_ALL_ITEMS);

            List<Item> items = new LinkedList<>();
            while(results.next()){
                Item item = new Item();
                item.setId(results.getString(COLUMN_ITEM_ID));
                item.setName(results.getString(COLUMN_ITEM_NAME));
                item.setPages(results.getInt(COLUMN_ITEM_PAGES));
                item.setPackageSize(results.getInt(COLUMN_ITEM_PACKAGE));
                item.setPrice(results.getDouble(COLUMN_ITEM_PRICE));
                item.setStock(results.getInt(COLUMN_ITEM_STOCK));
                item.setProductID(results.getString(COLUMN_ITEM_PRODUCT));

                items.add(item);
            }

            return items;
        } catch (SQLException e) {
            System.out.println("Error query items: " + e.getMessage());
            return null;
        }
    }

}
