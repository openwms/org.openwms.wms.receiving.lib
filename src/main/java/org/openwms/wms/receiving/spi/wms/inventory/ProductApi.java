package org.openwms.wms.receiving.spi.wms.inventory;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "wms-inventory", decode404 = true, qualifiers = "productApi")
public interface ProductApi {

    /**
     * Gets {@code Product} based on {@code ProductUnit} pKey
     *
     * @param pKey The pKey of the productUnit
     */
    @GetMapping("/v1/product/product-units/{pKey}")
    ProductVO findProductByProductUnitPkey(@PathVariable("pKey") String pKey);
}
