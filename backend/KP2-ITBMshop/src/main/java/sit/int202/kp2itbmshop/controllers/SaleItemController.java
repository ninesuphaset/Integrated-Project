package sit.int202.kp2itbmshop.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.dtos.AddUpdateItemDto;
import sit.int202.kp2itbmshop.dtos.ItemDetailDto;
import sit.int202.kp2itbmshop.dtos.SaleItemsDto;
import sit.int202.kp2itbmshop.entities.SaleItem;
import sit.int202.kp2itbmshop.services.SaleItemService;
import sit.int202.kp2itbmshop.utils.ListMapper;

import java.util.List;

@RestController
@RequestMapping("/v1/sale-items")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class SaleItemController {
    @Autowired
    private SaleItemService saleitemService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ListMapper listMapper;

    @GetMapping("")
    public ResponseEntity<List<SaleItemsDto>> getAllSaleItems() {
        return ResponseEntity.ok(
                listMapper.mapList(saleitemService.getAllSaleItem()
                        ,SaleItemsDto.class,modelMapper)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemDetailDto> getSaleItemById(
            @PathVariable Integer id) {
//        return ResponseEntity.ok(
//                modelMapper.map(saleitemService.getSaleItemById(id)
//                        ,ItemDetailDto.class)
        return ResponseEntity.ok(
                saleitemService.getItemDetailDtoById(id)
        );
    }


    @PostMapping("")
    public ResponseEntity<ItemDetailDto> addSaleItem(
            @RequestBody AddUpdateItemDto addUpdateItemDto) {
        SaleItem saveItem = saleitemService.saveSaleItem(addUpdateItemDto);
        ItemDetailDto itemDetailDto = saleitemService.getItemDetailDtoById(saveItem.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(itemDetailDto);
    }


    @PutMapping("/{id}")
    public ResponseEntity<ItemDetailDto> updateSaleItem(
            @PathVariable Integer id,
            @RequestBody AddUpdateItemDto addUpdateItemDto) {
        addUpdateItemDto.setId(id);
        SaleItem updatedSaleItem = saleitemService.updateSaleItem(addUpdateItemDto);
        ItemDetailDto updatedItemDetailDto = saleitemService.getItemDetailDtoById(updatedSaleItem.getId());
        return ResponseEntity.ok(updatedItemDetailDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSaleItemById(@PathVariable Integer id) {
        saleitemService.deleteSaleItemById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
