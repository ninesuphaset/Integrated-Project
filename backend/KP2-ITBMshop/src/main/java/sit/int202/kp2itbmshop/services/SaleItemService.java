package sit.int202.kp2itbmshop.services;

import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sit.int202.kp2itbmshop.Exception.ItemNotFoundException;
import sit.int202.kp2itbmshop.dtos.*;
import sit.int202.kp2itbmshop.entities.*;
import sit.int202.kp2itbmshop.repositories.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SaleItemService {
    @Autowired
    private SaleItemRepository saleItemRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private ImageService imageService;
    @Autowired
    private SellerProfileRepository sellerProfileRepository;
    @Autowired
    private UserRepository userRepository;

    public List<SaleItem> getAllSaleItem() {
        return saleItemRepository.findAllByOrderByCreatedOnAscIdAsc();
    }

    public SaleItem getSaleItemById(Integer id) {
        return saleItemRepository.findById(id).orElseThrow(
                () -> new ItemNotFoundException("SaleItem not found for this id :: " + id)
        );
    }

    public SaleItem saveSaleItem(AddUpdateItemDto addUpdateItemDto) {
        addUpdateItemDto.setId(null);
        SaleItem saleitem = modelMapper.map(addUpdateItemDto, SaleItem.class);
        Brand brand = brandRepository.findById(addUpdateItemDto.getBrand().getId()).orElseThrow(
                () -> new ItemNotFoundException("Brand not found for this id : " + addUpdateItemDto.getBrand().getId())
        );
        if (addUpdateItemDto.getQuantity() == null || addUpdateItemDto.getQuantity() < 0) {
            saleitem.setQuantity(1);
        } else {
            saleitem.setQuantity(addUpdateItemDto.getQuantity());
        }
        saleitem.setBrand(brand);
        return saleItemRepository.save(saleitem);
    }

    public SaleItem updateSaleItem(AddUpdateItemDto addUpdateItemDto) {
        SaleItem existingSaleItem = saleItemRepository.findById(addUpdateItemDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "SaleItem with code " + addUpdateItemDto.getId() + " not found"));
        modelMapper.map(addUpdateItemDto, existingSaleItem);

        if (addUpdateItemDto.getBrand().getId() != null) {
            Brand brand = brandRepository.findById(addUpdateItemDto.getBrand().getId())
                    .orElseThrow(() -> new ItemNotFoundException("Brand not found for this id : "
                            + addUpdateItemDto.getBrand().getId()));
            existingSaleItem.setBrand(brand);
        }

        if (addUpdateItemDto.getQuantity() == null || addUpdateItemDto.getQuantity() < 0) {
            existingSaleItem.setQuantity(1);
        } else {
            existingSaleItem.setQuantity(addUpdateItemDto.getQuantity());
        }
        return saleItemRepository.save(existingSaleItem);
    }

    public void deleteSaleItemById(Integer id) {
        if (!saleItemRepository.existsById(id)) {
            throw new ItemNotFoundException("SaleItem not found for this id : " + id);
        }
        saleItemRepository.deleteById(id);
    }

    public Page<SaleItem> findAllByImplement(
            Integer page,
            Integer size,
            List<String> brandNames,
            Integer priceMin,
            Integer priceMax,
            List<Integer> storageGbs,
            String keyword,
            String sortField,
            String sortDirection) {

        Sort sort = Sort.unsorted();
        if (sortField != null && !sortField.isEmpty()) {
            Sort.Direction direction = Sort.Direction.ASC;
            if (sortDirection != null && !sortDirection.isBlank()) {
                direction = Sort.Direction.fromString(sortDirection);
            }
            sort = Sort.by(new Sort.Order(direction, sortField))
                    .and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = Sort.by(
                    new Sort.Order(Sort.Direction.ASC, "createdOn"),
                    new Sort.Order(Sort.Direction.ASC, "id"));
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Specification<SaleItem> spec = Specification.where(null);

        // brand filter
        if (brandNames != null && !brandNames.isEmpty()) {
            spec = spec.and((root, query, cb) -> root.get("brand").get("name").in(brandNames));
        }

        // storage filter
        if (storageGbs != null && !storageGbs.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                Predicate predicate = root.get("storageGb").in(storageGbs);
                if (storageGbs.contains(-1)) {
                    predicate = cb.or(predicate, cb.isNull(root.get("storageGb")));
                }
                return predicate;
            });
        }

        // price filter
        if (priceMin != null || priceMax != null) {
            int min = priceMin != null ? priceMin : Integer.MIN_VALUE;
            int max = priceMax != null ? priceMax : Integer.MAX_VALUE;
            spec = spec.and((root, query, cb) -> cb.between(root.get("price"), min, max));
        }

        // keyword filter (รองรับหลายคำ AND กัน)
        if (keyword != null && !keyword.isBlank()) {
            List<String> keywords = Arrays.stream(keyword.split("\\s+"))
                    .filter(k -> !k.isEmpty())
                    .toList();
            for (String k : keywords) {
                String pattern = "%" + k.toLowerCase() + "%";
                spec = spec.and((root, query, cb) -> cb.or(
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("model")), pattern),
                        cb.like(cb.lower(root.get("color")), pattern)
                ));
            }
        }

        return saleItemRepository.findAll(spec, pageRequest);
    }

    public ItemDetailDto createSaleItemWithImages(SaleItemCreateDto req, Integer sellerId) {
        User user = userRepository.findById(sellerId).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "User not found"));
        if(!user.getIsActive()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not active");

        SellerProfile seller = sellerProfileRepository.findByUserId(sellerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Seller not found or invalid token"));

        brandRepository.findById(req.getBrandId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Brand not found."));

        SaleItem saleItem = modelMapper.map(req, SaleItem.class);
        saleItem.setId(null);
        saleItem.setSeller(seller);

        SaleItem saved = saleItemRepository.save(saleItem);

        if (req.getImageInfos() != null) {
            for (ImageInfoDto info : req.getImageInfos()) {
                if ("NEW".equalsIgnoreCase(info.getStatus()) && info.getImageFile() != null) {
                    imageService.store(info.getImageFile(), saved, info.getOrder());
                }
            }
        }

        List<Image> images = imageService.getImagesBySaleItem(saved.getId());
        imageService.normalizePositions(images);

        return buildItemDetailDto(saved, images);
    }

    public ItemDetailDto updateSaleItemWithImages(Integer id, SaleItemWithImageInfo req) {
        SaleItem saleItem = saleItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SaleItem not found"));

        modelMapper.map(req, saleItem);
        SaleItem updated = saleItemRepository.save(saleItem);

        List<Image> currentImages = imageService.getImagesBySaleItem(id);

        if (req.getImageInfos() != null) {
            for (ImageInfoDto info : req.getImageInfos()) {
                if ("NEW".equalsIgnoreCase(info.getStatus()) && info.getImageFile() != null) {
                    imageService.store(info.getImageFile(), updated, info.getOrder());
                }
            }

            for (ImageInfoDto info : req.getImageInfos()) {
                if ("DELETE".equalsIgnoreCase(info.getStatus()) && info.getFileName() != null) {
                    currentImages.stream()
                            .filter(img -> img.getFileName().equalsIgnoreCase(info.getFileName()))
                            .findFirst()
                            .ifPresent(img -> imageService.deleteFile(img));
                }
            }

            Set<String> incomingNames = req.getImageInfos().stream()
                    .filter(info -> !"DELETE".equalsIgnoreCase(info.getStatus()))
                    .map(ImageInfoDto::getFileName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            for (Image img : currentImages) {
                if (!incomingNames.contains(img.getFileName())) {
                    imageService.deleteFile(img);
                }
            }

            Map<String, Integer> desiredOrder = new HashMap<>();
            for (ImageInfoDto info : req.getImageInfos()) {
                if (( "MOVE".equalsIgnoreCase(info.getStatus())
                        || "ONLINE".equalsIgnoreCase(info.getStatus())
                        || "NEW".equalsIgnoreCase(info.getStatus()) )
                        && info.getFileName() != null
                        && info.getOrder() != null) {
                    desiredOrder.put(info.getFileName(), info.getOrder());
                }
            }

            List<Image> refreshed = imageService.getImagesBySaleItem(id);
            for (Image img : refreshed) {
                Integer order = desiredOrder.get(img.getFileName());
                if (order != null) {
                    img.setPosition(order);
                    imageRepository.save(img);
                }
            }
        }

        List<Image> finalImages = imageService.getImagesBySaleItem(id);
        imageService.normalizePositions(finalImages);

        return buildItemDetailDto(updated, finalImages);
    }

    public ItemDetailDto getItemDetailDtoById(Integer id) {
        SaleItem item = getSaleItemById(id);
        List<Image> images = imageService.getImagesBySaleItem(id);
        return buildItemDetailDto(item, images);
    }

    private ItemDetailDto buildItemDetailDto(SaleItem saleItem, List<Image> images) {
        ItemDetailDto dto = new ItemDetailDto();
        dto.setId(saleItem.getId());
        dto.setModel(saleItem.getModel());
        dto.setBrandName(saleItem.getBrand().getName());
        dto.setDescription(saleItem.getDescription());
        dto.setPrice(saleItem.getPrice());
        dto.setRamGb(saleItem.getRamGb());
        dto.setStorageGb(saleItem.getStorageGb());
        dto.setColor(saleItem.getColor());
        dto.setScreenSizeInch(saleItem.getScreenSizeInch());
        dto.setQuantity(saleItem.getQuantity());
        dto.setCreatedOn(saleItem.getCreatedOn());
        dto.setUpdatedOn(saleItem.getUpdatedOn());

        dto.setSaleItemImages(images.stream().map(img -> {
            ItemDetailDto.SaleItemImageDto imgDto = new ItemDetailDto.SaleItemImageDto();
            imgDto.setFileName(img.getFileName());
            imgDto.setImageViewOrder(img.getPosition());
            imgDto.setStatus("ONLINE");
            return imgDto;
        }).collect(Collectors.toList()));

        if (saleItem.getSeller() != null && saleItem.getSeller().getUser() != null) {
            SellerDto sellerDto = new SellerDto();
            sellerDto.setId(saleItem.getSeller().getUser().getId());
            sellerDto.setUserName(saleItem.getSeller().getUser().getNickName());
            dto.setSeller(sellerDto);
        }

        return dto;
    }

    public PageDTO<ItemDetailDto> buildItemDetailPage(Page<SaleItem> saleItems) {
        PageDTO<ItemDetailDto> dto = new PageDTO<>();
        dto.setContent(
                saleItems.getContent().stream()
                        .map(item -> buildItemDetailDto(item, imageService.getImagesBySaleItem(item.getId())))
                        .toList()
        );
        dto.setFirst(saleItems.isFirst());
        dto.setLast(saleItems.isLast());
        dto.setTotalPages(saleItems.getTotalPages());
        dto.setTotalElements((int) saleItems.getTotalElements());
        dto.setSize(saleItems.getSize());
        dto.setNumber(saleItems.getNumber());
        return dto;
    }

    public PageDTO<ItemDetailDto> getSaleItemsBySeller(
            Integer userId,
            Integer page,
            Integer size,
            String sortField,
            String sortDirection) {

        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing or invalid user id");
        }
        // สร้าง sort
        Sort sort = Sort.unsorted();
        if (sortField != null && !sortField.isBlank()) {
            sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField)
                    .and(Sort.by(Sort.Order.asc("id")));
        } else {
            sort = Sort.by(Sort.Order.asc("createdOn"), Sort.Order.asc("id"));
        }

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        // ดึงข้อมูลแบบ Page
        Page<SaleItem> saleItems = saleItemRepository.findBySeller_User_Id(userId, pageRequest);

        // แปลงเป็น PageDTO<ItemDetailDto>
        return buildItemDetailPage(saleItems);
    }

}
