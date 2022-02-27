package hello.itemservice.domain.item;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MyItemRepository {

    private static final Map<Long, MyItem> store = new HashMap<>();
    private static long sequence = 0L;

    public MyItem save(MyItem myItem) {
        myItem.setItemId(++sequence);
        store.put(myItem.getItemId(), myItem);
        return myItem;
    }

    public MyItem findById(Long itemId) {
        return store.get(itemId);
    }

    public List<MyItem> findAll() {
        return new ArrayList<>(store.values());
    }

    public void update(Long itemId, MyItem updateItem){
        MyItem findItem = findById(itemId);

        findItem.setItemName(updateItem.getItemName());
        findItem.setPrice(updateItem.getPrice());
        findItem.setQuantity(updateItem.getQuantity());
    }

    public void clearStore() {
        store.clear();
    }
}
