import {Button} from '@chakra-ui/react'
import SidebarWithHeader from "./shared/SideBar.jsx";

const App = () => {
    return (
        <SidebarWithHeader>
            <Button colorScheme='blue'>Button</Button>
        </SidebarWithHeader>
    )
}

export default App;