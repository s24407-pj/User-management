import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    Icon,
    useDisclosure
} from "@chakra-ui/react";

import CreateCustomerForm from "./CreateCustomerForm.jsx";
import {FiUserPlus} from "react-icons/fi";

const CloseIcon = () => "X";

const CreateCustomerDrawer = ({fetchCustomers, size, text, onSuccess}) => {
    const {isOpen, onOpen, onClose} = useDisclosure()

    return (
        <>
            <Button
                variant={"ghost"}
                justifyContent={"left"}
                onClick={onOpen}
            >
                <Icon
                    as={FiUserPlus}
                    mr={"10px"}
                />
                {text}
            </Button>

            <Drawer isOpen={isOpen} onClose={onClose} size={size}>
                <DrawerOverlay/>
                <DrawerContent>
                    <DrawerCloseButton/>
                    <DrawerHeader>Create new customer</DrawerHeader>
                    <DrawerBody>
                        <CreateCustomerForm fetchCustomers={fetchCustomers} onClose={onClose} onSuccess={onSuccess}/>
                    </DrawerBody>
                    <DrawerFooter>
                        <Button
                            leftIcon={<CloseIcon/>}
                            onClick={onClose}
                            colorScheme={"teal"}
                        >
                            Close
                        </Button>
                    </DrawerFooter>
                </DrawerContent>
            </Drawer>
        </>
    )
}

export default CreateCustomerDrawer;

