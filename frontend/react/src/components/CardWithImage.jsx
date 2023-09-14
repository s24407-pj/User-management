'use client'

import {Avatar, Box, Button, Center, Flex, Heading, Image, Stack, Tag, Text, useColorModeValue,} from '@chakra-ui/react'
import DeleteCustomerButton from "./DeleteCustomerButton.jsx";
import UpdateCustomerDrawer from "./UpdateCustomerDrawer.jsx";

// eslint-disable-next-line react/prop-types
export default function CardWithImage({id, name, email, age, gender, index, fetchCustomers}) {
    gender === "MALE" ? gender = "Man" : gender = "Woman"
    return (
        <Center py={6}>
            <Box
                maxW={'300px'}
                w={'full'}
                bg={useColorModeValue('white', 'gray.800')}
                boxShadow={'2xl'}
                rounded={'md'}
                overflow={'hidden'}>
                <Image
                    h={'120px'}
                    w={'full'}
                    src={
                        'https://images.unsplash.com/photo-1612865547334-09cb8cb455da?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=634&q=80'
                    }
                    objectFit="cover"
                    alt="#"
                />
                <Flex justify={'center'} mt={-12}>
                    <Avatar
                        size={'xl'}
                        src={
                            `https://randomuser.me/api/portraits/med/${gender === "Man" ? "men" : "women"}/${id}.jpg`
                        }
                        css={{
                            border: '2px solid white',
                        }}
                    />
                </Flex>

                <Box p={6}>
                    <Stack spacing={0} align={'center'} mb={5}>
                        <Tag borderRadius={"full"}>{++index}</Tag>
                        <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
                            {name}
                        </Heading>
                        <Text color={'gray.500'}>{email}</Text>
                        <Text color={'gray.500'}>Age {age} | {gender}</Text>
                    </Stack>
                </Box>
                <Box>
                    <Stack
                        align={'center'}
                        direction='row'
                        m={5}
                        justifyContent={'space-evenly'}
                    >
                        <DeleteCustomerButton
                            id={id}
                            name={name}
                            fetchCustomers={fetchCustomers}
                        />
                        <UpdateCustomerDrawer
                            initialValues={{name, email, age, gender}}
                            fetchCustomers={fetchCustomers}
                            customerId={id}
                        />
                    </Stack>
                </Box>

            </Box>
        </Center>
    )
}