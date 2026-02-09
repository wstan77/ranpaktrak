import { StyleSheet } from "react-native";
import { useContext, useEffect, useState } from "react";

import { ScrollView, View, Text } from "@app/components/Themed";
import { Content, Divisor } from "@app/components";
import { useMachinesQuery } from "@app/service/MachineService";
import { useStorageStateSimple } from "@app/hooks/useStorageState";
import { STORAGE_KEYS } from "@app/service/helpers/storage";
import { MachineData } from "@app/model/MachineData";
import { useCustomersQuery } from "@app/service/CustomerService";
import { useDateFormat } from "@app/hooks/useDateFormat";
import { AppContext } from "@app/service/contexts/AppContext";
import { ScanData } from "@app/model/ScanData";
import { sortMachinesBySerial } from "@app/service/helpers/arrays";

export default function MachineListScreen() {
  const machines = useMachinesQuery();
  const myMachines = typeof machines?.data === "object" ? machines.data : [];
  const selectedCustomer = useStorageStateSimple(STORAGE_KEYS.selectedCustomer);

  const appContext = useContext(AppContext);
  const currentScans = appContext?.ranpakScans ?? [];

  const [machinesList, setMachinesList] = useState<MachineData[] | ScanData[]>(
    []
  );
  const {
    data: { customers },
  } = useCustomersQuery();

  const { getISOFormat } = useDateFormat();

  useEffect(() => {
    populateMachines();
  }, [selectedCustomer?.customerId]);

  const populateMachines = () => {
    const customerMachines = myMachines?.filter(
      (machine) => machine.customerId === selectedCustomer?.customerId
    );
    const savedMachines = currentScans?.filter(
      (mh) => mh.customerId === selectedCustomer?.customerId
    );
    const allMachines = sortMachinesBySerial([
      ...customerMachines,
      ...savedMachines,
    ]);
    setMachinesList(allMachines);
  };
  const distributorCustomer = (ID: number) => {
    return customers.find(c => c.customerId === ID);
  };

  const serialNumberAssociated = (serialNumber: string) => {
    return myMachines?.filter(
      (machine) =>
        machine.customerId === selectedCustomer?.customerId &&
        machine.serialNumber === serialNumber
    );
  };
  const filterAndUpdateMachines = (machines: MachineData[]): MachineData[] => {
    const machinesWithIds = machines.filter(machine => machine.machineId);
    return machinesWithIds;
  };
  const filteredMachines = filterAndUpdateMachines(machinesList);
  return (
    <>
      <View style={styles.container}>
        <Text style={styles.title}>Machines Last Reported at Location</Text>
        <Divisor />
        <Content>
          <ScrollView>
            {machinesList.length === 0 ? (
              <Text>No machines added</Text>
            ) : (
              filteredMachines?.map((machine, i) => {
                const isCurrentlyScanned = currentScans.some(scan => scan.serialNumber === machine.serialNumber);
                const scanInfo = currentScans.find(scan => scan.serialNumber === machine.serialNumber);
                return (
                  <View
                    key={i}
                    style={[styles.item, i === 0 && styles.firstItem]}
                  >
                    <View style={styles.itemContainer}>
                      <Text>
                        {machine.serialNumber}{" "}
                        {machine.legacySerialNumber &&
                          `(${machine.legacySerialNumber})`}
                      </Text>
                      <Text>
                        Type:{" "}
                        <Text style={styles.bold}>
                          {machine.machineTypeLk
                            ? machine.machineTypeLk
                            : "Unknown"}
                        </Text>
                      </Text>
                      {
                      machine.distributorCustomerId &&
                      distributorCustomer(machine.distributorCustomerId)?.accountName && (
                        <Text>
                          Distributor: {distributorCustomer(machine.distributorCustomerId).accountName}
                        </Text>
                      )
                    }
                      <Text>Last Verified: {isCurrentlyScanned ? getISOFormat(scanInfo.scanDate, true) : getISOFormat(machine.scanTmstp, true)}</Text>
                    </View>
                    <View style={styles.statuses}>
                      <View style={styles.row}>
                        {!serialNumberAssociated(machine.serialNumber) && (
                          <View style={styles.new}>
                            <Text>NEW</Text>
                          </View>
                        )}
                        {isCurrentlyScanned && (
                          <View style={styles.scanned}>
                            <Text style={styles.white}>SCANNED</Text>
                          </View>
                        )}
                      </View>
                    </View>
                  </View>
                )
              })
            )}
          </ScrollView>
        </Content>
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, paddingBottom: 30 },
  itemContainer: { flex: 1 },
  title: {
    fontWeight: "bold",
    fontSize: 18,
    textAlign: "center",
    marginVertical: 22,
  },
  firstItem: {
    borderTopWidth: 1,
    borderTopColor: "#bebebe",
  },
  item: {
    display: "flex",
    flexDirection: "row",
    width: "100%",
    borderLeftWidth: 1,
    borderLeftColor: "#bebebe",
    borderRightWidth: 1,
    borderRightColor: "#bebebe",
    borderBottomWidth: 1,
    borderBottomColor: "#bebebe",
    paddingHorizontal: 10,
    paddingVertical: 15,
  },
  row: {
    display: "flex",
    flexDirection: "row",
    gap: 3,
  },
  statuses: {
    gap: 3,
    alignItems: "center",
  },
  new: {
    backgroundColor: "orange",
    paddingHorizontal: 5,
    paddingVertical: 3,
    height: 32,
    justifyContent: "center",
  },
  scanned: {
    backgroundColor: "green",
    paddingHorizontal: 5,
    paddingVertical: 3,
    height: 32,
    justifyContent: "center",
  },
  white: { color: "#fff" },
  bold: { fontWeight: "bold" },
});
