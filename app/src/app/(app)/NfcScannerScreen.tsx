import {useEffect} from "react";
//import NfcManager, { NfcTech, TagEvent, Ndef } from 'react-native-nfc-manager';

export default function NfcScannerScreen({ navigation }) {
  /** /
  useEffect(() => {
    initNfc();
    return () => {
      NfcManager.unregisterTagEvent();
    };
  }, []);

  const initNfc = async () => {
    await NfcManager.start();
    await NfcManager.requestTechnology(NfcTech.Ndef);
    const tag = await NfcManager.getTag();
    console.log("Found Tag =>", tag);
    await handleTagEvent(tag);
  };

  const handleTagEvent = async (tagEvent: TagEvent) => {
    const tagMessage = tagEvent.ndefMessage;
    if (tagMessage) {
      const firstRecord = tagMessage[0];
      if (Number(firstRecord.type) === Ndef.TNF_WELL_KNOWN && Ndef.RTD_TEXT) {
        const tagData = Ndef.text.decodePayload(new Uint8Array(firstRecord.payload));
        await handleTagDiscovered(tagEvent.id, tagData);
      }
    }
  };

  const handleTagDiscovered = (tagId: string, tagData: string) => {};

  return (
    <></>
  );
  /**/
}
