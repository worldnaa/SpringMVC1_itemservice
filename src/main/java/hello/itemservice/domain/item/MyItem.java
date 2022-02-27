package hello.itemservice.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MyItem {

    private Long itemId;
    private String itemName;
    private Integer price;
    private Integer quantity;

    public MyItem() {
    }

    public MyItem(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
