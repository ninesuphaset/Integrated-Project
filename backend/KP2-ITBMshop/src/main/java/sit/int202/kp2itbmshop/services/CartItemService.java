package sit.int202.kp2itbmshop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sit.int202.kp2itbmshop.Exception.ItemNotFoundException;
import sit.int202.kp2itbmshop.dtos.CartItemDto;
import sit.int202.kp2itbmshop.dtos.SellerCartDto;
import sit.int202.kp2itbmshop.entities.CartItem;
import sit.int202.kp2itbmshop.entities.Image;
import sit.int202.kp2itbmshop.entities.SaleItem;
import sit.int202.kp2itbmshop.entities.User;
import sit.int202.kp2itbmshop.repositories.CartItemRepository;
import sit.int202.kp2itbmshop.repositories.SaleItemRepository;
import sit.int202.kp2itbmshop.repositories.UserRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartItemService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    // Helper method สำหรับ map Image -> SaleItemImageDto
    public static CartItemDto.SaleItemImageDto buildSaleItemImageDto(Image img) {
        CartItemDto.SaleItemImageDto dto = new CartItemDto.SaleItemImageDto();
        dto.setFileName(img.getFileName());
        dto.setImageViewOrder(img.getPosition());
        dto.setStatus("ONLINE");
        return dto;
    }

    // สร้าง CartItemDto พร้อมรูป (ไม่มี seller)
    private CartItemDto buildCartItemDto(CartItem cartItem, List<Image> images) {
        CartItemDto dto = new CartItemDto();
        dto.setCartItemId(cartItem.getId());
        dto.setUserId(cartItem.getUser().getId());

        SaleItem saleItem = cartItem.getSaleitem();
        dto.setSaleitemId(saleItem.getId());
        dto.setModel(saleItem.getModel());
        dto.setBrandName(saleItem.getBrand().getName());
        dto.setStorageGb(saleItem.getStorageGb());
        dto.setColor(saleItem.getColor());
        dto.setPrice(saleItem.getPrice());
        dto.setStock(saleItem.getQuantity());

        // จำนวนใน cart
        dto.setQuantity(cartItem.getQuantity());

        // รูปสินค้า
        dto.setSaleItemImages(images.stream()
                .map(img -> CartItemService.buildSaleItemImageDto(img))
                .collect(Collectors.toList()));
        System.out.println("Images for saleItem " + saleItem.getId() + ": " + images.size());

        return dto;

    }

    public List<SellerCartDto> getCartItemsGroupedBySeller(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);

        // map พร้อมรูป
        List<CartItemDto> cartDtos = cartItems.stream().map(item -> {
            List<Image> images = imageService.getImagesBySaleItem(item.getSaleitem().getId());
            return buildCartItemDto(item, images);
        }).collect(Collectors.toList());

        // group by sellerId
        Map<Integer, List<CartItemDto>> grouped = new HashMap<>();
        Map<Integer, String> sellerNames = new HashMap<>(); // สร้าง map สำหรับชื่อ seller

        for (CartItem item : cartItems) {
            Integer sellerId = item.getSaleitem().getSeller().getUser().getId();
            String sellerName = item.getSaleitem().getSeller().getUser().getNickName();

            grouped.computeIfAbsent(sellerId, k -> new ArrayList<>());
            grouped.get(sellerId).add(
                    cartDtos.stream()
                            .filter(dto -> dto.getCartItemId().equals(item.getId()))
                            .findFirst()
                            .get()
            );

            sellerNames.putIfAbsent(sellerId, sellerName); // เก็บชื่อ seller
        }

        // map to SellerCartDto
        List<SellerCartDto> result = new ArrayList<>();
        for (Map.Entry<Integer, List<CartItemDto>> entry : grouped.entrySet()) {
            SellerCartDto sellerCart = new SellerCartDto();
            sellerCart.setSellerId(entry.getKey());
            sellerCart.setSellerName(sellerNames.get(entry.getKey())); // ใช้ชื่อ seller
            sellerCart.setCart(entry.getValue());
            result.add(sellerCart);
        }

        return result;
    }

    // เพิ่มสินค้าใน cart
    public CartItemDto addToCart(Integer userId, Integer saleitemId, Integer quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SaleItem saleItem = saleItemRepository.findById(saleitemId)
                .orElseThrow(() -> new RuntimeException("SaleItem not found"));

        if (quantity > saleItem.getQuantity()) {
            throw new RuntimeException("Quantity exceeds available stock");
        }

        CartItem cartItem = cartItemRepository.findByUserAndSaleitemId(user, saleitemId).orElse(null);

        if (cartItem != null) {
            int newQty = cartItem.getQuantity() + quantity;
            if (newQty > saleItem.getQuantity()) {
                throw new RuntimeException("Quantity exceeds available stock");
            }
            cartItem.setQuantity(newQty);
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setSaleitem(saleItem);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedOn(Instant.now());
            cartItem.setUpdatedOn(Instant.now());
        }

        cartItem = cartItemRepository.save(cartItem);

        List<Image> images = imageService.getImagesBySaleItem(saleItem.getId());
        return buildCartItemDto(cartItem, images);
    }

    // ลบสินค้าใน cart
    public void removeFromCart(Integer cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ItemNotFoundException("Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    // อัปเดต quantity ของ cart item
    public CartItemDto updateCartItemQuantity(Integer cartItemId, Integer newQuantity) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (newQuantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }

        if (newQuantity > cartItem.getSaleitem().getQuantity()) {
            throw new RuntimeException("Quantity exceeds available stock");
        }

        cartItem.setQuantity(newQuantity);
        cartItem = cartItemRepository.save(cartItem);

        List<Image> images = imageService.getImagesBySaleItem(cartItem.getSaleitem().getId());
        return buildCartItemDto(cartItem, images);
    }
}