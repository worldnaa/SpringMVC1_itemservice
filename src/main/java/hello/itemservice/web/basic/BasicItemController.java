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

    // 상품 목록
    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items"; // 상품 목록 뷰로 이동
    }

    // 상품 상세
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item"; // 상품 상세 뷰로 이동
    }

    // 상품 등록 폼
    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm"; // 상품 등록 폼 뷰로 이동
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     /* 상품 등록 처리 1) @RequestParam

     - 먼저 @RequestParam 을 통해 각각의 요청 파라미터 데이터를 해당 변수에 받는다.
     - Item 객체를 생성하고 itemRepository 를 통해서 저장한다.
     - 저장된 item 을 모델에 담아서 뷰에 전달한다.*/

    // @PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                            @RequestParam int price,
                            @RequestParam Integer quantity,
                            Model model) {
        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item); // 상품등록(저장)
        model.addAttribute("item", item); // 등록된 상품의 상세화면을 위해 모델에 저장
        return "basic/item"; // 상품 상세 뷰로 이동
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     /* 상품 등록 처리 2) @ModelAttribute

     [ @ModelAttribute - 요청 파라미터 처리 ]
     - @ModelAttribute 는 Item 객체를 생성하고, 요청 파라미터의 값을 프로퍼티 접근법(setXxx)으로 입력해준다.

     [ @ModelAttribute - Model 추가 ]
     - @ModelAttribute 는 모델(Model) 에 @ModelAttribute 로 지정한 객체를 자동으로 넣어준다.
     - 모델에 데이터를 담을 때는 이름이 필요하다. 이름은 @ModelAttribute 에 지정한 name(value) 속성을 사용한다.
     - ex) @ModelAttribute("hello") Item item => 이름을 hello 로 지정
     - ex) model.addAttribute("hello", item); => 모델에 hello 이름으로 저장 됨 */

    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {
        itemRepository.save(item);
        return "basic/item";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 상품 등록 처리 3) ModelAttribute 이름 생략

    - @ModelAttribute 의 이름을 생략할 수 있다.
    - @ModelAttribute 의 이름을 생략하면 모델에 저장될 때 클래스명을 사용한다.
    - 이때 클래스의 첫 글자만 소문자로 변경해서 등록한다.
    - ex) @ModelAttribute Item item              ==> item 으로 저장
    - ex) @ModelAttribute HelloWorld helloWorld  ==> helloWorld 로 저장 */

    // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /* 상품 등록 처리 4) ModelAttribute 전체 생략

    - @ModelAttribute 자체를 생략 가능
    - 대상 객체는 모델에 자동 등록된다. 나머지 사항은 기존과 동일하다. */

    // @PostMapping("/add")
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    // BUT. addItemV1 ~ addItemV4 까지는 등록 후 새로고침하면 중복 저장되는 문제가 발생한다!!!
    // 웹 브라우저의 새로고침은 마지막에 서버에 전송한 데이터를 다시 전송한다.
    // 상품 등록 폼에서 데이터를 입력하고 저장을 선택하면 'POST /add + 상품 데이터' 를 서버로 전송한다.
    // 이 상태에서 새로고침을 또 선택하면 마지막에 전송한 'POST /add + 상품 데이터' 를 서버로 다시 전송하게 된다.
    // 그래서 내용은 같고, ID만 다른 상품 데이터가 계속 쌓이게 된다.

    // 새로고침 문제를 해결하려면 상품 저장후에 뷰템플릿으로 이동하는 것이 아니라, 상품상세화면으로 리다이렉트를 호출해주면된다.
    // 웹 브라우저는 리다이렉트의 영향으로 상품 저장후에 실제 상품상세화면으로 다시 이동한다.
    // 따라서 마지막에 호출한 내용이 상품상세화면인 'GET /items/{id}' 가 되는 것 이다.
    // 이후 새로고침을 해도 상품상세화면으로 이동하게 되므로 새로고침 문제를 해결할 수 있다.

    //////////////////////////////////////// PRG (Post/Redirect/Get) 방식 ///////////////////////////////////////////////

    // [ URL 에 변수를 더해서 사용하는 방식 ] => URL 인코딩이 안 되기 때문에 위험하다
    // @PostMapping("/add")
    public String addItemV5(Item item) {
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId(); // 상품 상세 화면으로 리다이렉트
    }

    // [ RedirectAttributes 방식 ]
    // 리다이렉트할 때 간단히 status=true 를 추가해보자. 그리고 뷰 템플릿에서 이 값이 있으면, "저장되었습니다" 라는 메시지를 출력해보자.
    // 실행결과 : http://localhost:8080/basic/items/3?status=true
    // RedirectAttributes 를 사용하면 URL 인코딩도 해주고, pathVarible, 쿼리 파라미터까지 처리해준다.
    // ex) redirect:/basic/items/{itemId}
    // ==> pathVariable 바인딩: {itemId}
    // ==> 나머지는 쿼리 파라미터로 처리: ?status=true
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes) {
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/basic/items/{itemId}";
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // 상품 수정 폼
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/editForm"; // 상품 수정 폼 뷰로 이동
    }

    // 상품 수정 처리
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/basic/items/{itemId}"; // 상품 상세 화면으로 리다이렉트
    }

    /*
    - 상품수정은 상품등록과 전체 프로세스가 유사하다. (/items/{itemId}/edit 은 동일, Get or Post 로 구분)
    - 상품수정은 마지막에 뷰 템플릿을 호출하는 대신 상품 상세화면으로 이동하도록 리다이렉트를 호출한다.
    - 스프링은 redirect:/... 으로 편리하게 리다이렉트를 지원한다.            ex) redirect:/basic/items/{itemId}"
    - 컨트롤러에 매핑된 @PathVariable 의 값은 redirect 에도 사용할 수 있다.  ex) {itemId} 는 @PathVariable Long itemId 의 값을 그대로 사용
    */

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
