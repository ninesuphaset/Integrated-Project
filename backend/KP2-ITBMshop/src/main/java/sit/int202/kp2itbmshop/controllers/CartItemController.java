package sit.int202.kp2itbmshop.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sit.int202.kp2itbmshop.dtos.AddCartRequestDto;
import sit.int202.kp2itbmshop.dtos.CartItemDto;
import sit.int202.kp2itbmshop.dtos.SellerCartDto;
import sit.int202.kp2itbmshop.dtos.UpdateCartRequestDto;
import sit.int202.kp2itbmshop.services.CartItemService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/carts")
@CrossOrigin(origins = "${app.cors.allowedOrigins}")
public class CartItemController {

    @Autowired
    private CartItemService cartItemService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<SellerCartDto>> getCartItems(@PathVariable Integer userId) {
        List<SellerCartDto> cartItems = cartItemService.getCartItemsGroupedBySeller(userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("")
    public ResponseEntity<?> addToCart(@RequestBody AddCartRequestDto request) {
        try {
            CartItemDto dto = cartItemService.addToCart(
                    request.getUserId(),
                    request.getSaleItemId(),
                    request.getQuantity()
            );
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer id) {
        try {
            cartItemService.removeFromCart(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCartItemQuantity(
            @PathVariable Integer id,
            @RequestBody UpdateCartRequestDto request) {
        try {
            CartItemDto updated = cartItemService.updateCartItemQuantity(id, request.getQuantity());
            if (updated == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}