/*
 * Copyright 2005-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.wms.receiving.spi.wms.inventory;

import java.io.Serializable;
import java.util.Objects;

/**
 * A CreatePackagingUnitCommand.
 *
 * @author Heiko Scherrer
 */
public class CreatePackagingUnitCommand implements Serializable {

    private String transportUnitBK;
    private String luPos;
    private String loadUnitType;
    private PackagingUnitVO packagingUnit;

    public CreatePackagingUnitCommand(){}

    public CreatePackagingUnitCommand(String transportUnitBK, String luPos, String loadUnitType, PackagingUnitVO packagingUnit) {
        this.transportUnitBK = transportUnitBK;
        this.luPos = luPos;
        this.loadUnitType = loadUnitType;
        this.packagingUnit = packagingUnit;
    }

    public String getTransportUnitBK() {
        return transportUnitBK;
    }

    public void setTransportUnitBK(String transportUnitBK) {
        this.transportUnitBK = transportUnitBK;
    }

    public String getLuPos() {
        return luPos;
    }

    public void setLuPos(String luPos) {
        this.luPos = luPos;
    }

    public String getLoadUnitType() {
        return loadUnitType;
    }

    public void setLoadUnitType(String loadUnitType) {
        this.loadUnitType = loadUnitType;
    }

    public PackagingUnitVO getPackagingUnit() {
        return packagingUnit;
    }

    public void setPackagingUnit(PackagingUnitVO packagingUnit) {
        this.packagingUnit = packagingUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreatePackagingUnitCommand)) return false;
        CreatePackagingUnitCommand that = (CreatePackagingUnitCommand) o;
        return Objects.equals(transportUnitBK, that.transportUnitBK) && Objects.equals(luPos, that.luPos) && Objects.equals(loadUnitType, that.loadUnitType) && Objects.equals(packagingUnit, that.packagingUnit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transportUnitBK, luPos, loadUnitType, packagingUnit);
    }

    @Override
    public String toString() {
        return "CreatePackagingUnitCommand{" +
                "transportUnitBK='" + transportUnitBK + '\'' +
                ", luPos='" + luPos + '\'' +
                ", loadUnitType='" + loadUnitType + '\'' +
                ", packagingUnit=" + packagingUnit +
                '}';
    }
}
