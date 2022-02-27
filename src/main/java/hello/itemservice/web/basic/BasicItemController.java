package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import java.util.List;

// 컨트롤러 로직 : itemRepository 에서 모든 상품을 조회한 다음에 모델에 담는다. 그리고 뷰 템플릿을 호출한다.

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor // final 이 붙은 멤버 변수만 사용해서 생성자를 자동으로 만들어준다.
public class BasicItemController {

    private final ItemRepository itemRepository;

    /*////////////// @RequiredArgsConstructor 사용 시 아래와 같은 생성자 자동 생성 ////////////////

    // 생성자가 딱 1개만 있으면 스프링이 해당 생성자에 @Autowired 로 의존관계를 주입해준다 (@Autowired 생략가능)
    // 따라서 final 키워드를 빼면 안된다! 그러면 ItemRepository 의존관계 주입이 안된다

    public BasicItemController(ItemRepository itemRepository) {
          this.itemRepository = itemRepository; //주입
    }
    ////////////////////////////////////////////////////////////////////////////////////////*/

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

//    @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item); //상품등록

        model.addAttribute("item", item); //등록된 상품 상세화면을 위해 모델에 저장

        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {

        itemRepository.save(item); //상품등록
//      model.addAttribute("item", item); //@ModelAttribute("item")에서 item 이름으로 자동추가 (생략가능)

        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) { // 생략시 클래스명의 첫글자를 소문자로 바꿔서 모델에 저장
        itemRepository.save(item); //상품등록
        return "basic/item";
    }

//    @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item); //상품등록
        return "basic/item"; //등록 후 새로고침하면 중복 저장되는 문제 발생!
    }

//    @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item); //상품등록
        return "redirect:/basic/items/" + item.getId();
    }

    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);//상품등록
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}";
    }

    /**
     * 테스트용 데이터 추가
     * @PostConstruct : 해당 빈의 의존관계가 모두 주입되고 나면 초기화 용도로 호출된다.
     */
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
