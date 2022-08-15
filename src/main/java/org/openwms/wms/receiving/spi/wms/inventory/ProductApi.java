package org.openwms.wms.receiving.spi.wms.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "wms-inventory", decode404 = true, qualifiers = "productApi")
public interface ProductApi {

    /** API version. */
    String API_VERSION = "v1";
    /** API root to hit Products (plural). */
    String API_PRODUCTS = "/" + API_VERSION + "/products";

    /**
     * Gets {@code Product} based on {@code ProductUnit} pKey
     *
     * @param pKey The pKey of the productUnit
     */
    @GetMapping("/v1/product/product-units/{pKey}")
    ProductVO findProductByProductUnitPkey(@PathVariable("pKey") String pKey);

    /**
     * Find and return a {@code Product}.
     *
     * @param bk The business key, either the SKU or the Label
     * @return The instance
     */
    @GetMapping(value = API_PRODUCTS, params = {"bk"})
    ProductVO findByLabelOrSKU(@RequestParam("bk") String bk);
}
