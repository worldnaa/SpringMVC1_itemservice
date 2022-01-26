package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository // @Repository 안에 @Component 가 있어서, Component scan 대상이 된다
public class ItemRepository {

    // 스프링 컨테이너 안에선 어차피 싱글톤이라 굳이 static 을 사용하지 않아도 되나,
    // 따로 new 해서 사용하거나 할 때, static 을 안 하면, 인스턴스 생성할 때 마다 필드가 생긴다.
    // 실제로는 동시에 여러 쓰레드가 접근할 경우(싱글톤) HashMap 사용하면 안 된다 (사용하고 싶으면 ConcurrentHashMap 사용)
    // 마찬가지로 sequence에서도 동시 접근 시, long을 사용하면 꼬일 수 있어서 다른걸 사용해야 한다
    private static final Map<Long, Item> store = new HashMap<>();
    private static long sequence = 0L;

    // 상품 저장
    public Item save(Item item) { 
        item.setId(++sequence);
        store.put(item.getId(), item);
        return item;
    }
    
    // 상품 조회 (상품 id로 조회)
    public Item findById(Long id) {
        return store.get(id); // value 리턴
    }

    // 상품 전체 조회
    public List<Item> findAll() {
        return new ArrayList<>(store.values());
    }

    // 상품 수정
    public void update(Long itemId, Item updateParam){
        Item findItem = findById(itemId); // 수정할 상품 조회

        //정석은 ItemParamDto 같은 객체를 만들어서, 아래 3가지 파라미터만 넣어둔다
        findItem.setItemName(updateParam.getItemName());
        findItem.setPrice(updateParam.getPrice());
        findItem.setQuantity(updateParam.getQuantity());
    }

    // 테스트를 위해 store 초기화
    public void clearStore() {
        store.clear();
    }
}
