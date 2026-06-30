//package com.example.qafilah.features.catalog.data.mapper
//
//import com.example.qafilah.features.catalog.domain.model.detail.OptionValue
//import com.example.qafilah.features.catalog.domain.model.detail.ProductDetail
//import com.example.qafilah.features.catalog.domain.model.detail.ProductImage
//import com.example.qafilah.features.catalog.domain.model.detail.ProductVariant
//import com.example.qafilah.graphql.storefront.GetProductQuery
//
//object ProductDetailMapper {
//
//    fun toDomain(product: GetProductQuery.Product): ProductDetail {
//        val domainImages = product.images.edges.map { edge ->
//            ProductImage(
//                url = edge.node.url.toString(),
//                altText = edge.node.altText
//            )
//        }
//
//        val domainVariants = product.variants.edges.map { edge ->
//            val node = edge.node
//            ProductVariant(
//                id = node.id,
//                title = node.title,
//                price = node.price.amount.toString(),
//                inventoryQuantity = node.quantityAvailable,
//                selectedOptions = node.selectedOptions.map { opt ->
//                    OptionValue(
//                        name = opt.name,
//                        value = opt.value
//                    )
//                }
//            )
//        }
//
//        val domainMetafields = product.metafields.orEmpty().filterNotNull().associate { metafield ->
//            "${metafield.namespace}.${metafield.key}" to metafield.value
//        }
//
//        return ProductDetail(
//            id = product.id,
//            title = product.title,
//            descriptionHtml = product.descriptionHtml.toString(),
//            images = domainImages,
//            variants = domainVariants,
//            metafields = domainMetafields
//        )
//    }
//}
