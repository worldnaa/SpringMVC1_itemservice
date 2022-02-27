package hello.itemservice.domain.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class MyItemRepositoryTest {

    MyItemRepository myItemRepository = new MyItemRepository();

    @AfterEach
    void afterEach() {
        myItemRepository.clearStore();
    }

    @Test
    void save() {
        //given
        MyItem myItem = new MyItem("item1", 10000, 10);

        //when
        MyItem saveItem = myItemRepository.save(myItem);

        //then
        MyItem findItem = myItemRepository.findById(myItem.getItemId());
        assertThat(findItem).isEqualTo(saveItem);
    }

    @Test
    void findAll() {
        //given
        MyItem myItem1 = new MyItem("item1", 10000, 10);
        MyItem myItem2 = new MyItem("item2", 20000, 20);

        myItemRepository.save(myItem1);
        myItemRepository.save(myItem2);

        //when
        List<MyItem> myItems = myItemRepository.findAll();

        //then
        assertThat(myItems.size()).isEqualTo(2);
        assertThat(myItems).contains(myItem1, myItem2);
    }

    @Test
    void updateItem() {
        //given
        MyItem myItem = new MyItem("item1", 10000, 10);

        MyItem saveItem = myItemRepository.save(myItem);
        Long itemId = saveItem.getItemId();

        //when
        MyItem updateItem = new MyItem("item2", 20000, 20);
        myItemRepository.update(itemId, updateItem);

        //then
        MyItem findItem = myItemRepository.findById(itemId);

        assertThat(findItem.getItemName()).isEqualTo(updateItem.getItemName());
        assertThat(findItem.getPrice()).isEqualTo(updateItem.getPrice());
        assertThat(findItem.getQuantity()).isEqualTo(updateItem.getQuantity());
    }
}
