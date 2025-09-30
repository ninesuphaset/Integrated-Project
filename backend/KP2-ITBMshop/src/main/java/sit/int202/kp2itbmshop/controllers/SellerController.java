package sit.int202.kp2itbmshop.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.dtos.ItemDetailDto;
import sit.int202.kp2itbmshop.dtos.PageDTO;
import sit.int202.kp2itbmshop.dtos.SaleItemCreateDto;
import sit.int202.kp2itbmshop.services.SaleItemService;
import sit.int202.kp2itbmshop.utils.JwtUtil;

import java.util.Map;


@RestController
@RequestMapping("/v2/sellers")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class SellerController {
    @Autowired
    private SaleItemService saleItemService;
    @Autowired
    private JwtUtil jwtUtil;

    public SellerController(SaleItemService saleItemService) {
        this.saleItemService = saleItemService;
    }

    @PostMapping(value = "/{id}/sale-items", consumes = {"multipart/form-data"})
    public ResponseEntity<ItemDetailDto> createSaleItem(
            @Valid @ModelAttribute SaleItemCreateDto req,
            @PathVariable Integer id,
            @RequestHeader("Authorization")  String authHeader
            ){

        Map<String, Object> tokenInfo = jwtUtil.getInfo(authHeader, id);
        Integer tokenUserId = (Integer) tokenInfo.get("userId");
        ItemDetailDto created = saleItemService.createSaleItemWithImages(req, tokenUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}/sale-items")
    public ResponseEntity<PageDTO<ItemDetailDto>> getSaleItemsBySeller(
            @PathVariable("id") Integer id,
            @RequestParam Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestHeader("Authorization") String authHeader) {

        Map<String, Object> tokenInfo = jwtUtil.getInfo(authHeader, id);
        Integer tokenUserId = (Integer) tokenInfo.get("userId");
        PageDTO<ItemDetailDto> result = saleItemService.getSaleItemsBySeller(
                tokenUserId, page, size, sortField, sortDirection
        );

        return ResponseEntity.ok(result);
    }
}
