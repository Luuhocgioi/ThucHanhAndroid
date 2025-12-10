package clc65.hoangluu.duan.data;

import android.provider.BaseColumns;

public final class ProductContract {
    private ProductContract() {}

    // Bảng 1: Cache Sản phẩm (Giữ nguyên)
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products_cache";
        public static final String COLUMN_NAME_FIREBASE_ID = "firebase_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_IMAGE_URL = "image_url";
    }

    // Bảng 2: Giỏ hàng Tạm thời (MỚI THÊM)
    public static class CartEntry implements BaseColumns {
        public static final String TABLE_NAME = "local_cart";
        public static final String COLUMN_NAME_PRODUCT_ID = "product_id"; // ID sản phẩm
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_UNIT_PRICE = "unit_price"; // Giá tại thời điểm thêm
        public static final String COLUMN_NAME_NOTE = "note";
    }
}