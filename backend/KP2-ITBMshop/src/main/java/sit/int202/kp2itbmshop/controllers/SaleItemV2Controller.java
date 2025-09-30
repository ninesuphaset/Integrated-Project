package sit.int202.kp2itbmshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.dtos.ItemDetailDto;
import sit.int202.kp2itbmshop.dtos.PageDTO;
import sit.int202.kp2itbmshop.dtos.SaleItemCreateDto;
import sit.int202.kp2itbmshop.dtos.SaleItemWithImageInfo;
import sit.int202.kp2itbmshop.entities.SaleItem;
import sit.int202.kp2itbmshop.services.SaleItemService;
import sit.int202.kp2itbmshop.utils.JwtUtil;

import java.util.List;

@RestController
@RequestMapping("/v2/sale-items")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class SaleItemV2Controller {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("")
    public ResponseEntity<PageDTO<ItemDetailDto>> getSaleItems(
            @RequestParam(required = false) List<String> filterBrands,
            @RequestParam(required = false) Integer filterPriceMin,
            @RequestParam(required = false) Integer filterPriceMax,
            @RequestParam(required = false) List<Integer> filterStorage,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection
    ) {
        Page<SaleItem> saleItems = saleItemService.findAllByImplement(
                page, size, filterBrands, filterPriceMin, filterPriceMax, filterStorage, keyword, sortField, sortDirection
        );
        PageDTO<ItemDetailDto> pageDTO = saleItemService.buildItemDetailPage(saleItems);
        return ResponseEntity.ok(pageDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailDto> getSaleItemById(@PathVariable Integer id) {
        ItemDetailDto dto = saleItemService.getItemDetailDtoById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(consumes = {"multipart/form-data"})

    public ResponseEntity<ItemDetailDto> createSaleItem(
            @ModelAttribute SaleItemCreateDto req) {
        Integer sellerId = 1;
        ItemDetailDto created = saleItemService.createSaleItemWithImages(req, sellerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ItemDetailDto> updateSaleItem(
            @PathVariable Integer id,
            @ModelAttribute SaleItemWithImageInfo req) {
        ItemDetailDto updated = saleItemService.updateSaleItemWithImages(id, req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSaleItem(@PathVariable Integer id) {
        saleItemService.deleteSaleItemById(id);
        return ResponseEntity.noContent().build();
    }
}
