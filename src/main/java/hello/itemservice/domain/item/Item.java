package hello.itemservice.domain.item;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

// @Data : @Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode 만들어준다
// @Data 는 핵심 도메인에서 사용할 경우 예측하지 못하게 동작할 경우가 있어 사용이 위험하다 (필요한 것만 사용 권장)
@Getter @Setter
public class Item {

    private Long id;
    private String itemName;
    private Integer price;    //가격이 안 들어갈 수도 있기에 Integer 사용 (int는 0이라도 들어가야함)
    private Integer quantity; //수량이 안 들어갈 수도 있기에 Integer 사용 (int는 0이라도 들어가야함)

    public Item() {
    }

    public Item(String itemName, Integer price, Integer quantity) {
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
    }
}
